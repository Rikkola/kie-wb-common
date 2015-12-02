/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.projecteditor.client.forms.whitelist;

import java.util.ArrayList;
import java.util.Collection;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.forms.whitelist.WhiteListPresenter;
import org.kie.workbench.common.screens.projecteditor.client.forms.whitelist.WhiteListView;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.DependencyTestUtils.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WhiteListPresenterTest {

    @Mock
    WhiteListView view;

    @Mock
    PackageNameWhiteListService whiteListService;

    @Mock
    DependencyService dependencyService;

    private WhiteListPresenter whiteListPresenter;

    @Before
    public void setUp() throws Exception {
        whiteListPresenter = new WhiteListPresenter( view,
                                                     whiteListService,
                                                     new CallerMock<DependencyService>( dependencyService ) );
    }

    @Test
    public void testShowAvailablePackages() throws Exception {
        GAV gav = new GAV();
        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.add( makeDependency( "artifactID", "groupID", "version" ) );
        when( dependencyService.loadDependencies( gav ) ).thenReturn( dependencies );

        whiteListPresenter.show( gav );

        ArgumentCaptor<Collection> collectionArgumentCaptor = ArgumentCaptor.forClass( Collection.class );

        verify( view, never() ).setAvailablePackageNamesDisabled();
        verify( view, never() ).showNoDependencies();
        verify( view ).setAvailableDependencies( collectionArgumentCaptor.capture() );
        verify( view ).show();

        Collection collection = collectionArgumentCaptor.getValue();
        assertEquals( 1, collection.size() );
        assertEquals( "artifactID:groupID:version", collection.iterator().next() );
    }

    @Test
    public void testShowAvailablePackagesEmptyDependencyList() throws Exception {
        GAV gav = new GAV();
        when( dependencyService.loadDependencies( gav ) ).thenReturn( new ArrayList<Dependency>() );

        whiteListPresenter.show( gav );

        verify( view ).setAvailablePackageNamesDisabled();
        verify( view ).showNoDependencies();
        verify( view, never() ).setAvailableDependencies( anyCollection() );
        verify( view ).show();
    }
}