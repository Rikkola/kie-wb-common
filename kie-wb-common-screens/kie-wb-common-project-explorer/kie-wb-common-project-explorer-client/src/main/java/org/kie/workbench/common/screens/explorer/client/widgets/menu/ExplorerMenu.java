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
package org.kie.workbench.common.screens.explorer.client.widgets.menu;

import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContext;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsContext;
import org.uberfire.workbench.model.menu.Menus;

public class ExplorerMenu {

    private ProjectExplorerOptionsContext activeOptions;

    private ProjectContext context;

    private ExplorerMenuView    view;
    private ActiveViewPresenter activeViewPresenter;

    public ExplorerMenu() {

    }

    @Inject
    public ExplorerMenu( final ExplorerMenuView view,
                         final ProjectExplorerOptionsContext activeOptions,
                         final ProjectContext projectContext ) {
        this.view = view;
        this.activeOptions = activeOptions;
        this.context = projectContext;

        view.setPresenter( this );
    }

    public Menus asMenu() {
        return view.asMenu();
    }

    public void refresh() {
        if ( activeOptions.getOptions().isTreeNavigatorVisible() ) {
            view.showTreeNav();
        } else {
            view.showBreadcrumbNav();
        }

        if ( activeOptions.getOptions().isTechnicalViewActive() ) {
            view.showTechViewIcon();
            view.hideBusinessViewIcon();
        } else {
            view.showBusinessViewIcon();
            view.hideTechViewIcon();
        }

        if ( activeOptions.getOptions().canShowTag() ) {
            view.showTagFilterIcon();
        } else {
            view.hideTagFilterIcon();
        }
    }

    public void onBusinessViewSelected() {
        if ( !activeOptions.getOptions().isBusinessViewActive() ) {
            activeOptions.activateBusinessView();
            refresh();
            activeViewPresenter.update();
        }
    }

    public void onTechViewSelected() {
        if ( !activeOptions.getOptions().isTechnicalViewActive() ) {
            activeOptions.activateTechView();
            refresh();
            activeViewPresenter.update();
        }
    }

    public void onTreeExplorerSelected() {
        if ( !activeOptions.getOptions().isTreeNavigatorVisible() ) {
            activeOptions.activateTreeViewNavigation();
            refresh();
            activeViewPresenter.update();
        }
    }

    public void onBreadCrumbExplorerSelected() {
        if ( !activeOptions.getOptions().isBreadCrumbNavigationVisible() ) {
            activeOptions.activateBreadCrumbNavigation();
            refresh();
            activeViewPresenter.update();
        }
    }

    public void onShowTagFilterSelected() {
        if ( activeOptions.getOptions().canShowTag() ) {
            activeOptions.disableTagFiltering();
        } else {
            activeOptions.activateTagFiltering();
        }
        refresh();
        activeViewPresenter.update();
    }

    public void onArchiveActiveProject() {
        view.archive( context.getActiveProject().getRootPath() );
    }

    public void onArchiveActiveRepository() {
        view.archive( context.getActiveRepository().getRoot() );
    }

    public void onRefresh() {
        this.activeViewPresenter.init();
    }

    public void setActiveViewPresenter( final ActiveViewPresenter activeViewPresenter ) {
        this.activeViewPresenter = activeViewPresenter;
    }
}
