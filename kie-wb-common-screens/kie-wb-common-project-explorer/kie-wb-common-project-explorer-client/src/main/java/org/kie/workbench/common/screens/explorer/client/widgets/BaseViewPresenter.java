/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.asset.management.social.AssetManagementEventTypes;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.social.ProjectEventType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.social.activities.model.ExtendedTypes;
import org.kie.uberfire.social.activities.model.SocialFileSelectedEvent;
import org.kie.workbench.common.screens.explorer.client.utils.URLHelper;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsChangeHandler;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsContext;
import org.kie.workbench.common.screens.explorer.client.widgets.branches.BranchChangeHandler;
import org.kie.workbench.common.screens.explorer.client.widgets.itemactions.ItemCopier;
import org.kie.workbench.common.screens.explorer.client.widgets.itemactions.ItemDeleter;
import org.kie.workbench.common.screens.explorer.client.widgets.itemactions.ItemRenamer;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.client.widgets.tagSelector.TagChangedEvent;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.model.URIStructureExplorerModel;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerOptions;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

@SuppressWarnings( "CdiManagedBeanInconsistencyInspection" )
public abstract class BaseViewPresenter
        implements BranchChangeHandler,
                   ProjectExplorerOptionsChangeHandler {

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Caller<ExplorerService> explorerService;

    @Inject
    protected Caller<VFSService> vfsService;

    protected ProjectExplorerOptions projectExplorerOptions;
    protected String currentTag = null;
    @Inject
    private ProjectExplorerContextItems   projectExplorerContextItems;
    @Inject
    private ProjectExplorerContextManager projectExplorerContextManager;
    @Inject
    private ProjectContext                context;
    @Inject
    private ItemRenamer                   itemRenamer;
    @Inject
    private ItemDeleter                   itemDeleter;

    private boolean isOnLoading = false;
    private BaseViewImpl baseView;

    protected Set<String> activeContentTags = new TreeSet<String>();
    @Inject
    private ItemCopier itemCopier;

    public BaseViewPresenter( BaseViewImpl baseView ) {
        this.baseView = baseView;
    }

    @PostConstruct
    public void init() {
        baseView.init( this );
    }

    public void init( final ProjectExplorerOptionsContext activeOptionsContext ) {
        this.projectExplorerOptions = activeOptionsContext.getOptions();
        activeOptionsContext.addChangeHandler( this );
        projectExplorerContextManager.init( baseView,
                                            projectExplorerOptions,
                                            getContentCallback() );

    }

    public void onActiveOptionsChange() {
        final boolean isVisible = isViewVisible();
        setVisible( isVisible );
        if ( isVisible ) {
            initialiseViewForActiveContext();
        }
    }

    protected abstract boolean isViewVisible();

    public void update() {
        baseView.showHiddenFiles( projectExplorerOptions.areHiddenFilesVisible() );

        baseView.setNavType( getNavType() );

        if ( projectExplorerOptions.isHeaderNavigationHidden() ) {
            baseView.hideHeaderNavigator();
        }

        if ( projectExplorerOptions.canShowTag() ) {
            baseView.showTagFilter();
            projectExplorerContextManager.refresh();
        } else {
            baseView.hideTagFilter();
            if ( projectExplorerContextItems.getActiveContent() != null ) {
                baseView.setItems( projectExplorerContextItems.getActiveContent() );
            }
        }
    }

    private Explorer.NavType getNavType() {
        if ( projectExplorerOptions.isTreeNavigatorVisible() ) {
            return Explorer.NavType.TREE;
        } else {
            return Explorer.NavType.BREADCRUMB;
        }
    }

    public void refresh() {
        baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        projectExplorerContextManager.refresh();
    }

    public void loadContent( final FolderItem item ) {
        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( FolderListing fl ) {
                baseView.getExplorer().loadContent( fl );
            }
        } ).getFolderListing( projectExplorerContextItems.getActiveOrganizationalUnit(),
                              projectExplorerContextItems.getActiveRepository(),
                              projectExplorerContextItems.getActiveBranch(),
                              projectExplorerContextItems.getActiveProject(),
                              item,
                              projectExplorerOptions );
    }

    public FolderListing getActiveContent() {
        return projectExplorerContextItems.getActiveContent();
    }

    public void deleteItem( final FolderItem folderItem ) {
        itemDeleter.delete( folderItem,
                            new Command() {
                                @Override
                                public void execute() {
                                    projectExplorerContextManager.refresh();
                                }
                            } );

    }

    public void renameItem( final FolderItem folderItem ) {
        itemRenamer.rename( getFolderItemPath( folderItem ),
                            folderItem,
                            new Command() {
                                @Override
                                public void execute() {
                                    refresh();
                                }
                            } );
    }

    public void copyItem( final FolderItem folderItem ) {
        itemCopier.copy( folderItem,
                         getFolderItemPath( folderItem ),
                         new Command() {
                             @Override
                             public void execute() {

                                 refresh();
                             }
                         } );

    }

    public void uploadArchivedFolder( final FolderItem folderItem ) {
        if ( folderItem.getItem() instanceof Path ) {
            final Path path = ( Path ) folderItem.getItem();

            Window.open( URLHelper.getDownloadUrl( path ),
                         "downloading",
                         "resizable=no,scrollbars=yes,status=no" );
        }
    }

    private Path getFolderItemPath( final FolderItem folderItem ) {
        if ( folderItem.getItem() instanceof Package ) {
            final Package pkg = (( Package ) folderItem.getItem());
            return pkg.getPackageMainSrcPath();
        } else if ( folderItem.getItem() instanceof Path ) {
            return (Path) folderItem.getItem();
        }
        return null;
    }

    private void loadContent( final FolderListing content ) {
        if ( !projectExplorerContextItems.getActiveContent().equals( content ) ) {
            setActiveContent( content );
            baseView.getExplorer().loadContent( content );
        }
    }

    protected void setActiveContent( final FolderListing activeContent ) {
        projectExplorerContextItems.setActiveContent( activeContent );
        resetTags( false );
    }

    protected void resetTags( final boolean maintainSelection ) {
        if ( !projectExplorerOptions.canShowTag() ) {
            return;
        }
        if ( !maintainSelection ) {
            currentTag = null;
        }
        activeContentTags.clear();
        for ( FolderItem item : projectExplorerContextItems.getActiveContent().getContent() ) {
            if ( item.getTags() != null ) {
                activeContentTags.addAll( item.getTags() );
            }
        }
    }

    public String getCurrentTag() {
        return currentTag;
    }

    public Set<String> getActiveContentTags() {
        return activeContentTags;
    }

    private RemoteCallback<ProjectExplorerContent> getContentCallback() {
        return new RemoteCallback<ProjectExplorerContent>() {
            @Override
            public void callback( final ProjectExplorerContent content ) {
                doContentCallback( content );
            }

        };
    }

    //Process callback in separate method to better support testing
    void doContentCallback( final ProjectExplorerContent content ) {

        setActiveContent( content.getFolderListing() );

        baseView.getExplorer().clear();
        baseView.setContent( content.getOrganizationalUnits(),
                             projectExplorerContextItems.getActiveOrganizationalUnit(),
                             projectExplorerContextItems.getRepositories(),
                             projectExplorerContextItems.getActiveRepository(),
                             content.getProjects(),
                             projectExplorerContextItems.getActiveProject(),
                             projectExplorerContextItems.getActiveContent(),
                             content.getSiblings() );

        baseView.hideBusyIndicator();
    }

    public void onOrganizationalUnitSelected( final OrganizationalUnit organizationalUnit ) {
        if ( Utils.hasOrganizationalUnitChanged( organizationalUnit,
                                                 projectExplorerContextItems.getActiveOrganizationalUnit() ) ) {
            baseView.getExplorer().clear();

            baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            projectExplorerContextManager.refresh( new ProjectExplorerContentQuery( organizationalUnit ) );
        }
    }

    public void onRepositorySelected( final Repository repository ) {
        if ( Utils.hasRepositoryChanged( repository,
                                         projectExplorerContextItems.getActiveRepository() ) ) {
            baseView.getExplorer().clear();

            baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            projectExplorerContextManager.refresh( new ProjectExplorerContentQuery( projectExplorerContextItems.getActiveOrganizationalUnit(),
                                                                           repository,
                                                                           repository.getDefaultBranch() ) );
        }
    }

    @Override
    public void onBranchSelected( final String branch ) {
        if ( Utils.hasBranchChanged( branch,
                                     projectExplorerContextItems.getActiveBranch() ) ) {
            baseView.getExplorer().clear();

            baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            projectExplorerContextManager.refresh( new ProjectExplorerContentQuery( projectExplorerContextItems.getActiveOrganizationalUnit(),
                                                                           projectExplorerContextItems.getActiveRepository(),
                                                                           branch ) );
        }
    }

    public void onProjectSelected( final Project project ) {
        if ( Utils.hasProjectChanged( project,
                                      projectExplorerContextItems.getActiveProject() ) ) {
            baseView.getExplorer().clear();

            baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            projectExplorerContextManager.refresh( new ProjectExplorerContentQuery( projectExplorerContextItems.getActiveOrganizationalUnit(),
                                                                           projectExplorerContextItems.getActiveRepository(),
                                                                           projectExplorerContextItems.getActiveBranch(),
                                                                           project ) );
        }
    }

    public void onActiveFolderItemSelected( final FolderItem item ) {
        if ( !isOnLoading && Utils.hasFolderItemChanged( item, projectExplorerContextItems.getActiveFolderItem() ) ) {
            projectExplorerContextItems.setActiveFolderItem( item );
            projectExplorerContextItems.fireContextChangeEvent();

            //Show busy popup. Once Items are loaded it is closed
            baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            explorerService.call( new RemoteCallback<FolderListing>() {
                                      @Override
                                      public void callback( final FolderListing folderListing ) {
                                          isOnLoading = true;
                                          loadContent( folderListing );
                                          baseView.setItems( folderListing );
                                          baseView.hideBusyIndicator();
                                          isOnLoading = false;
                                      }
                                  },
                                  new HasBusyIndicatorDefaultErrorCallback( baseView ) ).getFolderListing( projectExplorerContextItems.getActiveOrganizationalUnit(),
                                                                                                           projectExplorerContextItems.getActiveRepository(),
                                                                                                           projectExplorerContextItems.getActiveBranch(),
                                                                                                           projectExplorerContextItems.getActiveProject(),
                                                                                                           item,
                                                                                                           projectExplorerOptions );
        }
    }

    public void onItemSelected( final FolderItem folderItem ) {
        final Object _item = folderItem.getItem();
        if ( _item == null ) {
            return;
        }
        if ( folderItem.getType().equals( FolderItemType.FILE ) && _item instanceof Path ) {
            placeManager.goTo( (Path) _item );
        } else {
            onActiveFolderItemSelected( folderItem );
        }
    }

    public boolean isVisible() {
        return baseView.isVisible();
    }

    public void setVisible( final boolean visible ) {
        baseView.setVisible( visible );
    }

    public void onTagFilterChanged( @Observes TagChangedEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        if ( !projectExplorerOptions.canShowTag() ) {
            return;
        }
        filterByTag( event.getTag() );

    }

    protected void filterByTag( final String tag ) {
        currentTag = tag;
        List<FolderItem> filteredItems = new ArrayList<FolderItem>();

        for ( FolderItem item : projectExplorerContextItems.getActiveContent().getContent() ) {
            if ( tag == null || item.getTags().contains( tag ) || item.getType().equals( FolderItemType.FOLDER ) ) {
                filteredItems.add( item );
            }
        }

        FolderListing filteredContent = new FolderListing( projectExplorerContextItems.getActiveContent().getItem(), filteredItems, projectExplorerContextItems.getActiveContent().getSegments() );
        baseView.renderItems( filteredContent );
    }

    // Refresh when a Resource has been updated, if it exists in the active package
    public void onResourceUpdated( @Observes final ResourceUpdatedEvent event ) {
        refresh( event.getPath() );
    }

    // Refresh when a Resource has been added, if it exists in the active package
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        refresh( event.getPath() );
    }

    // Refresh when a Resource has been deleted, if it exists in the active package
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        refresh( event.getPath() );
    }

    // Refresh when a Resource has been copied, if it exists in the active package
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        refresh( event.getDestinationPath() );
    }

    // Refresh when a lock status changes has occurred, if it affects the active package
    public void onLockStatusChange( @Observes final LockInfo lockInfo ) {
        refresh( lockInfo.getFile(), true );
    }

    private void refresh( final Path resource ) {
        refresh( resource, false );
    }

    private void refresh( final Path resource,
                          boolean force ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        if ( resource == null || projectExplorerContextItems.getActiveProject() == null ) {
            return;
        }
        if ( !force && !Utils.isInFolderItem( projectExplorerContextItems.getActiveFolderItem(),
                                              resource ) ) {
            return;
        }

        explorerService.call( new RemoteCallback<FolderListing>() {
            @Override
            public void callback( final FolderListing folderListing ) {
                projectExplorerContextItems.setActiveContent( folderListing );
                if ( projectExplorerOptions.canShowTag() ) {
                    resetTags( true );
                    filterByTag( currentTag );
                } else {
                    baseView.setItems( folderListing );
                }
            }
        }, new DefaultErrorCallback() ).getFolderListing( projectExplorerContextItems.getActiveOrganizationalUnit(),
                                                          projectExplorerContextItems.getActiveRepository(),
                                                          projectExplorerContextItems.getActiveBranch(),
                                                          projectExplorerContextItems.getActiveProject(),
                                                          projectExplorerContextItems.getActiveFolderItem(),
                                                          projectExplorerOptions );
    }

    public void onSocialFileSelected( @Observes final SocialFileSelectedEvent event ) {
        vfsService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path path ) {
                openBestSuitedScreen( event.getEventType(), path );
                setupActiveContextFor( path );
            }
        } ).get( event.getUri() );
    }

    private void openBestSuitedScreen( final String eventType,
                                       final Path path ) {
        if ( isRepositoryEvent( eventType ) ) {
            //the event is relative to a Repository and not to a file.
            placeManager.goTo( "repositoryStructureScreen" );
        } else if ( isProjectEvent( eventType ) ) {
            placeManager.goTo( "projectScreen" );
        } else {
            placeManager.goTo( path );
        }
    }

    private boolean isRepositoryEvent( String eventType ) {
        if ( eventType == null || eventType.isEmpty() ) {
            return false;
        }

        if ( ExtendedTypes.NEW_REPOSITORY_EVENT.name().equals( eventType ) ||
                AssetManagementEventTypes.BRANCH_CREATED.name().equals( eventType ) ||
                AssetManagementEventTypes.PROCESS_START.name().equals( eventType ) ||
                AssetManagementEventTypes.PROCESS_END.name().equals( eventType ) ||
                AssetManagementEventTypes.ASSETS_PROMOTED.name().equals( eventType ) ||
                AssetManagementEventTypes.PROJECT_BUILT.name().equals( eventType ) ||
                AssetManagementEventTypes.PROJECT_DEPLOYED.name().equals( eventType ) ||
                AssetManagementEventTypes.REPOSITORY_CHANGE.name().equals( eventType ) ) {

            return true;
        }
        return false;
    }

    private boolean isProjectEvent( final String eventType ) {
        return ProjectEventType.NEW_PROJECT.name().equals( eventType );
    }


    /*

    TODO MOVE This


     */
    private void setupActiveContextFor( final Path path ) {

        explorerService.call( new RemoteCallback<URIStructureExplorerModel>() {
            @Override
            public void callback( final URIStructureExplorerModel model ) {

                baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
                projectExplorerContextManager.refresh( new ProjectExplorerContentQuery( model.getOrganizationalUnit(),
                                                                               model.getRepository(),
                                                                               model.getRepository().getDefaultBranch(),
                                                                               model.getProject() ) );
            }
        } ).getURIStructureExplorerModel( path );

    }

    public void initialiseViewForActiveContext() {
        baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        projectExplorerContextManager.refresh( new ProjectExplorerContentQuery( context.getActiveOrganizationalUnit(),
                                                                       context.getActiveRepository(),
                                                                       context.getActiveBranch(),
                                                                       context.getActiveProject(),
                                                                       context.getActivePackage() ) );
    }

    public void initialiseViewForActiveContext( String initPath ) {
        projectExplorerContextManager.initActiveContext( initPath );
    }

    // Refresh when a Resource has been renamed, if it exists in the active package
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        final Path sourcePath = event.getPath();
        final Path destinationPath = event.getDestinationPath();

        boolean refresh = false;
        if ( Utils.isInFolderItem( projectExplorerContextItems.getActiveFolderItem(),
                                   sourcePath ) ) {
            refresh = true;
        } else if ( Utils.isInFolderItem( projectExplorerContextItems.getActiveFolderItem(),
                                          destinationPath ) ) {
            refresh = true;
        }

        if ( refresh ) {
            explorerService.call( new RemoteCallback<FolderListing>() {
                                      @Override
                                      public void callback( final FolderListing folderListing ) {
                                          baseView.setItems( folderListing );
                                      }
                                  },
                                  new DefaultErrorCallback() ).getFolderListing( projectExplorerContextItems.getActiveOrganizationalUnit(),
                                                                                 projectExplorerContextItems.getActiveRepository(),
                                                                                 projectExplorerContextItems.getActiveBranch(),
                                                                                 projectExplorerContextItems.getActiveProject(),
                                                                                 projectExplorerContextItems.getActiveFolderItem(),
                                                                                 projectExplorerOptions );
        }
    }
}
