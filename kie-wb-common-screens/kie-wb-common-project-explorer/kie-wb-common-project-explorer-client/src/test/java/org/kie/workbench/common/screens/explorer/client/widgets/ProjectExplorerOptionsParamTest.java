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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsBuilder;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class ProjectExplorerOptionsParamTest {

    private final String mode;
    private final Option option1;
    private final Option option2;

    @GwtMock
    private Window window;

    public ProjectExplorerOptionsParamTest( final String mode,
                                            final Option option1,
                                            final Option option2 ) {
        this.mode = mode;
        this.option1 = option1;
        this.option2 = option2;
    }

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks( this );
    }

    @Test
    public void testParametersInPlaceRequest() throws Exception {

        PlaceRequest placeRequest = mock( PlaceRequest.class );
        when( placeRequest.getParameter( "mode", "" ) ).thenReturn( mode );

        final ProjectExplorerOptions projectExplorerOptions = new ProjectExplorerOptionsBuilder().build( placeRequest,
                                                                              new HashMap<String, List<String>>() );

        assertTrue( projectExplorerOptions.contains( option1 ) );
        assertTrue( projectExplorerOptions.contains( option2 ) );
    }

    @Test
    public void testParametersInURL() throws Exception {
        // Not sure how these are different from place request parameters,
        // I feel they are redundant. Afraid to remove them at this point.

        final HashMap<String, List<String>> parameterMap = new HashMap<String, List<String>>();
        final ArrayList<String> strings = new ArrayList<String>();
        strings.add( mode );
        parameterMap.put( "explorer_mode", strings );

        final ProjectExplorerOptions projectExplorerOptions = new ProjectExplorerOptionsBuilder().build( mock( PlaceRequest.class ),
                                                                              parameterMap );

        assertTrue( projectExplorerOptions.contains( option1 ) );
        assertTrue( projectExplorerOptions.contains( option2 ) );
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][]{
                {"business_tree", Option.BUSINESS_CONTENT, Option.TREE_NAVIGATOR},
                {"business_explorer", Option.BUSINESS_CONTENT, Option.BREADCRUMB_NAVIGATOR},
                {"tech_tree", Option.TECHNICAL_CONTENT, Option.TREE_NAVIGATOR},
                {"tech_explorer", Option.TECHNICAL_CONTENT, Option.BREADCRUMB_NAVIGATOR}
        } );
    }

}
