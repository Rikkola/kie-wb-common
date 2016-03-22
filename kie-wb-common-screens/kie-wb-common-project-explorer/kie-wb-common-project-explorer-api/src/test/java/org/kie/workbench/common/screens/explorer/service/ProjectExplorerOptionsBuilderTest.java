package org.kie.workbench.common.screens.explorer.service;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectExplorerOptionsBuilderTest {

    @Test
    public void testNoParameters() throws Exception {

        final ProjectExplorerOptions options = ActiveOptionsBuilder.getDefaultOptions();

        assertFalse( options.isEmpty() );

        assertTrue( options.isBusinessViewActive() );
        assertTrue( options.isBreadCrumbNavigationVisible() );
        assertFalse( options.areHiddenFilesVisible() );
    }

    @Test
    public void testParametersInPlaceRequest_business_tree() throws Exception {

        PlaceRequest placeRequest = mock( PlaceRequest.class );
        when( placeRequest.getParameter( "mode", "" ) ).thenReturn( "business_tree" );

        final ProjectExplorerOptions options = new ActiveOptionsBuilder().build( placeRequest,
                                                                        new HashMap<String, List<String>>() );

        assertFalse( options.isEmpty() );

        assertTrue( options.isBusinessViewActive() );
        assertTrue( options.isTreeNavigatorVisible() );
        assertFalse( options.areHiddenFilesVisible() );

    }
}