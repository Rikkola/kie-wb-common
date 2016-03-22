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
package org.kie.workbench.common.screens.explorer.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsBuilder;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsContext;
import org.kie.workbench.common.screens.explorer.client.widgets.menu.ExplorerMenu;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExplorerPresenterTest {

    private ExplorerPresenter explorer;

    @Mock
    private ExplorerMenu menu;

    @Mock
    private ActiveViewPresenter activeViewPresenter;

    @Mock
    private ProjectExplorerOptionsBuilder projectExplorerOptionsBuilder;

    @Captor
    private ArgumentCaptor<Callback> callbackArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        explorer = new ExplorerPresenter( mock( ExplorerView.class ),
                                          activeViewPresenter,
                                          projectExplorerOptionsBuilder,
                                          menu );
    }

    @Test
    public void testOnStartUpNoInitPath() throws Exception {

        PlaceRequest placeRequest = mock( PlaceRequest.class );


        explorer.onStartup( placeRequest );

        verify( projectExplorerOptionsBuilder ).build( placeRequest,
                                                       anyMap(),
                                                       callbackArgumentCaptor.capture() );


        final ProjectExplorerOptionsContext activeOptionsContext = mock( ProjectExplorerOptionsContext.class );
        callbackArgumentCaptor.getValue().callback( activeOptionsContext );

        verify( activeViewPresenter ).init( null,
                                            activeOptionsContext );
    }

    @Test
    public void testOnStartUpInitPath() throws Exception {

        PlaceRequest placeRequest = mock( PlaceRequest.class );
        when( placeRequest.getParameter( eq( "init_path" ),
                                         anyString() ) ).thenReturn( "something" );


        explorer.onStartup( placeRequest );

        verify( projectExplorerOptionsBuilder ).build( placeRequest,
                                                       anyMap(),
                                                       callbackArgumentCaptor.capture() );


        final ProjectExplorerOptionsContext activeOptionsContext = mock( ProjectExplorerOptionsContext.class );
        callbackArgumentCaptor.getValue().callback( activeOptionsContext );

        verify( activeViewPresenter ).init( "something",
                                            activeOptionsContext );
    }

    @Test
    public void testOnStartUpPath() throws Exception {

        PlaceRequest placeRequest = mock( PlaceRequest.class );
        when( placeRequest.getParameter( eq( "path" ),
                                         anyString() ) ).thenReturn( "something_else" );


        explorer.onStartup( placeRequest );

        verify( projectExplorerOptionsBuilder ).build( placeRequest,
                                              anyMap(),
                                              callbackArgumentCaptor.capture() );


        final ProjectExplorerOptionsContext activeOptionsContext = mock( ProjectExplorerOptionsContext.class );
        callbackArgumentCaptor.getValue().callback( activeOptionsContext );

        verify( activeViewPresenter ).init( "something_else",
                                            activeOptionsContext );
    }
}