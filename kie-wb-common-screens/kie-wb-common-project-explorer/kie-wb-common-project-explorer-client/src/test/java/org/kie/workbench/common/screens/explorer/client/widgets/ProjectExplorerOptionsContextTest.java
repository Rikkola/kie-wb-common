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

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsChangeHandler;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectExplorerOptionsContextTest {

    private ProjectExplorerOptionsContext       context;
    private ProjectExplorerOptionsChangeHandler handler;

    @Before
    public void setUp() throws Exception {
        context = new ProjectExplorerOptionsContext();
        handler = mock( ProjectExplorerOptionsChangeHandler.class );
        context.addChangeHandler( handler );
    }

    @Test
    public void testActivateBusinessView() throws Exception {
        verify( handler, never() ).onActiveOptionsChange();

        context.activateBusinessView();

        verify( handler ).onActiveOptionsChange();

        assertTrue( context.getOptions().isBusinessViewActive() );
        assertFalse( context.getOptions().isTechnicalViewActive() );
    }

    @Test
    public void testActivateTechView() throws Exception {
        verify( handler, never() ).onActiveOptionsChange();

        context.activateTechView();

        verify( handler ).onActiveOptionsChange();

        assertTrue( context.getOptions().isTechnicalViewActive() );
        assertFalse( context.getOptions().isBusinessViewActive() );
    }

    @Test
    public void testBreadCrumbNavigation() throws Exception {
        verify( handler, never() ).onActiveOptionsChange();

        context.activateBreadCrumbNavigation();

        verify( handler ).onActiveOptionsChange();

        assertTrue( context.getOptions().isBreadCrumbNavigationVisible() );
        assertFalse( context.getOptions().isTreeNavigatorVisible() );
    }

    @Test
    public void testTreeViewNavigation() throws Exception {
        verify( handler, never() ).onActiveOptionsChange();

        context.activateTreeViewNavigation();

        verify( handler ).onActiveOptionsChange();

        assertTrue( context.getOptions().isTreeNavigatorVisible() );
        assertFalse( context.getOptions().isBreadCrumbNavigationVisible() );
    }
}