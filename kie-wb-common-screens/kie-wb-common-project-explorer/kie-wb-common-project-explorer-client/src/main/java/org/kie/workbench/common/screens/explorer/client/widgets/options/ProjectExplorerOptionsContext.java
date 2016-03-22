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
package org.kie.workbench.common.screens.explorer.client.widgets.options;

import org.kie.workbench.common.screens.explorer.service.ProjectExplorerOptions;
import org.kie.workbench.common.screens.explorer.service.Option;

public class ProjectExplorerOptionsContext {

    private final ProjectExplorerOptions options;

    private ProjectExplorerOptionsChangeHandler handler;

    ProjectExplorerOptionsContext( final ProjectExplorerOptions options ) {
        this.options = options;
    }

    public ProjectExplorerOptions getOptions() {
        return options;
    }

    public void activateBusinessView() {
        options.add( Option.BUSINESS_CONTENT );
        options.remove( Option.TECHNICAL_CONTENT );

        fireChange();
    }

    public void activateTechView() {
        options.remove( Option.BUSINESS_CONTENT );
        options.add( Option.TECHNICAL_CONTENT );

        fireChange();
    }

    public void activateBreadCrumbNavigation() {
        options.add( Option.BREADCRUMB_NAVIGATOR );
        options.remove( Option.TREE_NAVIGATOR );

        fireChange();
    }

    public void activateTreeViewNavigation() {
        options.remove( Option.BREADCRUMB_NAVIGATOR );
        options.add( Option.TREE_NAVIGATOR );

        fireChange();
    }

    public void activateTagFiltering() {
        options.add( Option.SHOW_TAG_FILTER );

        fireChange();
    }

    public void disableTagFiltering() {
        options.remove( Option.SHOW_TAG_FILTER );

        fireChange();
    }

    private void fireChange() {
        if ( handler != null ) {
            handler.onActiveOptionsChange();
        }
    }


    public void addChangeHandler( final ProjectExplorerOptionsChangeHandler handler ) {
        this.handler = handler;
    }
}
