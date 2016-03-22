/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentUpdatedEvent;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerOptions;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;

public class ProjectExplorerContextManager {

    private ProjectExplorerContextItems projectExplorerContextItems;
    private Caller<ExplorerService>     explorerService;

    private BaseView baseView;

    private           ProjectExplorerOptions                 projectExplorerOptions;
    private           RemoteCallback<ProjectExplorerContent> contentCallback;
    private           RuntimeAuthorizationManager            authorizationManager;
    private           ProjectBuilder                         projectBuilder;
    private transient SessionInfo                            sessionInfo;


    public ProjectExplorerContextManager() {
    }

    @Inject
    public ProjectExplorerContextManager( final ProjectExplorerContextItems projectExplorerContextItems,
                                          final Caller<ExplorerService> explorerService,
                                          final RuntimeAuthorizationManager authorizationManager,
                                          final ProjectBuilder projectBuilder,
                                          final SessionInfo sessionInfo ) {
        this.projectExplorerContextItems = projectExplorerContextItems;
        this.explorerService = explorerService;
        this.authorizationManager = authorizationManager;
        this.projectBuilder = projectBuilder;
        this.sessionInfo = sessionInfo;
    }

    public void init( final BaseView baseView,
                      final ProjectExplorerOptions projectExplorerOptions,
                      final RemoteCallback<ProjectExplorerContent> contentCallback ) {
        this.baseView = baseView;
        this.projectExplorerOptions = projectExplorerOptions;
        this.contentCallback = contentCallback;
    }

    public void initActiveContext( final String path ) {
        baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        explorerService.call( getContentCallback(),
                              new HasBusyIndicatorDefaultErrorCallback( baseView ) ).getContent( path,
                                                                                                 projectExplorerOptions );
    }

    private RemoteCallback<ProjectExplorerContent> getContentCallback() {
        return new RemoteCallback<ProjectExplorerContent>() {

            @Override
            public void callback( final ProjectExplorerContent content ) {

                boolean signalChange = projectExplorerContextItems.setupActiveOrganizationalUnit( content );

                if ( projectExplorerContextItems.setupActiveRepository( content ) ) {
                    signalChange = true;
                }

                if ( projectExplorerContextItems.setupActiveBranch( content ) ) {
                    signalChange = true;
                }

                if ( projectExplorerContextItems.setupActiveProject( content ) ) {
                    signalChange = true;
                    projectBuilder.buildProject( projectExplorerContextItems.getActiveProject() );
                }

                boolean folderChange = projectExplorerContextItems.setupActiveFolderAndPackage(
                        content );
                if ( signalChange || folderChange ) {
                    projectExplorerContextItems.fireContextChangeEvent();
                }

                projectExplorerContextItems.setRepositories( content.getRepositories() );

                if ( projectExplorerContextItems.getActiveFolderItem() == null ) {
                    projectExplorerContextItems.setupActiveFolderAndPackage( content );
                }

                contentCallback.callback( content );
            }
        };
    }

    public void refresh( final ProjectExplorerContentQuery query ) {

        query.setOptions( projectExplorerOptions );

        explorerService.call( getContentCallback(),
                              new HasBusyIndicatorDefaultErrorCallback( baseView ) ).getContent( query );
    }

    private void refresh( final Project project ) {
        refresh( new ProjectExplorerContentQuery( projectExplorerContextItems.getActiveOrganizationalUnit(),
                                                  projectExplorerContextItems.getActiveRepository(),
                                                  projectExplorerContextItems.getActiveBranch(),
                                                  project ) );
    }

    void refresh() {
        refresh( new ProjectExplorerContentQuery( projectExplorerContextItems.getActiveOrganizationalUnit(),
                                                  projectExplorerContextItems.getActiveRepository(),
                                                  projectExplorerContextItems.getActiveBranch(),
                                                  projectExplorerContextItems.getActiveProject(),
                                                  projectExplorerContextItems.getActivePackage(),
                                                  projectExplorerContextItems.getActiveFolderItem() ) );
    }

    private boolean isInActiveBranch( final Project project ) {
        return Utils.isInBranch( getCurrentBranchRoot(),
                                 project );
    }

    private Path getCurrentBranchRoot() {
        if ( projectExplorerContextItems.getActiveRepository() == null ) {
            return null;
        } else {
            return projectExplorerContextItems.getActiveRepository().getBranchRoot( projectExplorerContextItems.getActiveBranch() );
        }
    }

    public void onBranchCreated( @Observes final NewBranchEvent event ) {
        if ( projectExplorerContextItems.isTheActiveRepository( event.getRepositoryAlias() ) ) {
            if ( projectExplorerContextItems.getActiveRepository() instanceof GitRepository ) {
                addBranch( projectExplorerContextItems.getActiveRepository(),
                           event.getBranchName(),
                           event.getBranchPath() );
            }
        }

        if ( projectExplorerContextItems.getRepositories() != null ) {
            for ( Repository repository : projectExplorerContextItems.getRepositories() ) {
                if ( repository.getAlias().equals( event.getRepositoryAlias() ) ) {
                    addBranch( repository,
                               event.getBranchName(),
                               event.getBranchPath() );
                }
            }
        }
    }

    private void addBranch( final Repository repository,
                            final String branchName,
                            final Path branchPath ) {
        (( GitRepository ) repository).addBranch( branchName,
                                                  branchPath );
        refresh();
    }

    // Refresh when a batch Resource change has occurred. Simply refresh everything.
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        if ( !baseView.isVisible() ) {
            return;
        }

        boolean projectChange = false;
        for ( final Path path : resourceBatchChangesEvent.getBatch().keySet() ) {
            if ( path.getFileName().equals( "pom.xml" ) ) {
                projectChange = true;
                break;
            }
        }

        if ( !projectChange ) {
            refresh();
        }
    }

    public void onSystemRepositoryChanged( @Observes final SystemRepositoryChangedEvent event ) {
        if ( baseView.isVisible() ) {
            refresh();
        }
    }

    public void onOrganizationalUnitAdded( @Observes final NewOrganizationalUnitEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        if ( organizationalUnit == null ) {
            return;
        }
        if ( authorizationManager.authorize( organizationalUnit,
                                             sessionInfo.getIdentity() ) ) {
            refresh();
        }
    }

    public void onOrganizationalUnitRemoved( @Observes final RemoveOrganizationalUnitEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        if ( organizationalUnit == null ) {
            return;
        }

        refresh();
    }

    public void onRepoAddedToOrganizationalUnitEvent( @Observes final RepoAddedToOrganizationalUnitEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        final Repository repository = event.getRepository();
        if ( repository == null ) {
            return;
        }
        if ( authorizationManager.authorize( repository,
                                             sessionInfo.getIdentity() ) ) {
            refresh();
        }
    }

    public void onRepoRemovedFromOrganizationalUnitEvent( @Observes final RepoRemovedFromOrganizationalUnitEvent event ) {
        if ( baseView.isVisible() ) {
            refresh();
        }
    }

    public void onRepositoryRemovedEvent( @Observes final RepositoryRemovedEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }

        // The following comparison must stay in that order to avoid a NullPointerException
        if ( event.getRepository().equals( projectExplorerContextItems.getActiveRepository() ) ) {
            projectExplorerContextItems.flush();
        }

        refresh();
    }

    public void onRepositoryUpdatedEvent( @Observes final RepositoryEnvironmentUpdatedEvent event ) {
        if ( projectExplorerContextItems.isTheActiveRepository( event.getUpdatedRepository().getAlias() ) ) {
            refresh();
        } else {
            projectExplorerContextItems.updateRepository( event.getUpdatedRepository().getAlias(),
                                                 event.getUpdatedRepository().getEnvironment() );
        }
    }

    public void onPackageAdded( @Observes final NewPackageEvent event ) {
        if ( !baseView.isVisible() ) {
            return;
        }
        final org.guvnor.common.services.project.model.Package pkg = event.getPackage();
        if ( pkg == null ) {
            return;
        }
        if ( !Utils.isInProject( projectExplorerContextItems.getActiveProject(),
                                 pkg ) ) {
            return;
        }

        refresh( new ProjectExplorerContentQuery(
                        projectExplorerContextItems.getActiveOrganizationalUnit(),
                        projectExplorerContextItems.getActiveRepository(),
                        projectExplorerContextItems.getActiveBranch(),
                        projectExplorerContextItems.getActiveProject(),
                        pkg ) );
    }

    public void onProjectAdded( @Observes final NewProjectEvent event ) {
        if ( baseView.isVisible() && event.getProject() != null ) {

            if ( sessionInfo.getId().equals( event.getSessionId() )
                    && isInActiveBranch( event.getProject() ) ) {

                refresh( event.getProject() );

            } else {

                refresh();

            }
        }
    }

    public void onProjectRename( @Observes final RenameProjectEvent event ) {
        if ( isInActiveBranch( event.getOldProject() ) ) {
            if ( authorizationManager.authorize( event.getOldProject(),
                                                 sessionInfo.getIdentity() ) ) {
                refresh( event.getNewProject() );
            }
        }
    }

    public void onProjectDelete( @Observes final DeleteProjectEvent event ) {
        if ( isInActiveBranch( event.getProject() ) && authorizationManager.authorize( event.getProject(),
                                                                                       sessionInfo.getIdentity() ) ) {
            if ( projectExplorerContextItems.getActiveProject() != null && projectExplorerContextItems.getActiveProject().equals( event.getProject() ) ) {
                projectExplorerContextItems.flushActiveProject();
            }

            baseView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            refresh( new ProjectExplorerContentQuery(
                    projectExplorerContextItems.getActiveOrganizationalUnit(),
                    projectExplorerContextItems.getActiveRepository(),
                    projectExplorerContextItems.getActiveBranch() ) );
        }
    }
}
