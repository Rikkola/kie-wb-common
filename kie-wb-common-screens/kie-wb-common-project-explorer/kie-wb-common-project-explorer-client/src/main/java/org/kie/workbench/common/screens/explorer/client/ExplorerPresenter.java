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
package org.kie.workbench.common.screens.explorer.client;

import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsBuilder;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsContext;
import org.kie.workbench.common.screens.explorer.client.widgets.menu.ExplorerMenu;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Repository, Package, Folder and File explorer
 */
@WorkbenchScreen(identifier = "org.kie.guvnor.explorer")
public class ExplorerPresenter {

    private ExplorerView view;

    public  ExplorerMenu                  menu;
    private ProjectExplorerOptionsBuilder projectExplorerOptionsBuilder;
    private ActiveViewPresenter           activeViewPresenter;
    private String                        initPath;

    public ExplorerPresenter() {
    }

    @Inject
    public ExplorerPresenter( final ExplorerView view,
                              final ActiveViewPresenter activeViewPresenter,
                              final ProjectExplorerOptionsBuilder projectExplorerOptionsBuilder,
                              final ExplorerMenu menu ) {
        this.view = view;
        this.activeViewPresenter = activeViewPresenter;
        this.projectExplorerOptionsBuilder = projectExplorerOptionsBuilder;
        this.menu = menu;

        this.menu.setActiveViewPresenter( activeViewPresenter );
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {

        setInitPath( placeRequest );

        projectExplorerOptionsBuilder.build( placeRequest,
                                             Window.Location.getParameterMap(),
                                             new Callback<ProjectExplorerOptionsContext>() {
                                                 @Override
                                                 public void callback( final ProjectExplorerOptionsContext activeOptionsContext ) {
                                                     init( activeOptionsContext );
                                                 }
                                             } );

    }

    public void init( final ProjectExplorerOptionsContext activeOptionsContext ) {

        menu.refresh();
        activeViewPresenter.init( initPath,
                                  activeOptionsContext );

    }

    @WorkbenchPartView
    public UberView<ExplorerPresenter> getView() {
        return this.view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectExplorerConstants.INSTANCE.explorerTitle();
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.WEST;
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return menu.asMenu();
    }

    private void setInitPath( final PlaceRequest placeRequest ) {
        initPath = placeRequest.getParameter( "path",
                                              null );
        initPath = placeRequest.getParameter( "init_path",
                                              initPath );
    }
}
