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
import java.util.HashSet;
import java.util.Iterator;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.projecteditor.client.forms.DependencyTestUtils.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WhiteListPresenterTest {

    @Mock
    WhiteListView view;

    @Mock
    DependencyService dependencyService;

    private WhiteListPresenter whiteListPresenter;

    @Before
    public void setUp() throws Exception {
        whiteListPresenter = new WhiteListPresenter( view,
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

        verify( view, never() ).setDependenciesListDisabled();
        verify( view, never() ).showNoDependencies();
        verify( view ).setAvailableDependencies( collectionArgumentCaptor.capture() );
        verify( view ).show();

        Collection collection = collectionArgumentCaptor.getValue();
        assertEquals( 1, collection.size() );
        assertEquals( "groupID:artifactID:version", collection.iterator().next() );
    }

    @Test
    public void testShowAvailablePackagesEmptyDependencyList() throws Exception {
        GAV gav = new GAV();
        when( dependencyService.loadDependencies( gav ) ).thenReturn( new ArrayList<Dependency>() );

        whiteListPresenter.show( gav );

        verify( view ).setDependenciesListDisabled();
        verify( view ).showNoDependencies();
        verify( view, never() ).setAvailableDependencies( anyCollection() );
        verify( view ).show();
    }

    @Test
    public void testSelectDependency() throws Exception {
        GAV gav = new GAV();
        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.add( makeDependency( "artifactID1", "groupID1", "version1" ) );
        dependencies.add( makeDependency( "artifactID2", "groupID2", "version2" ) );
        when( dependencyService.loadDependencies( gav ) ).thenReturn( dependencies );

        whiteListPresenter.show( gav );

        ArgumentCaptor<Collection> collectionArgumentCaptor = ArgumentCaptor.forClass( Collection.class );
        verify( view ).setAvailableDependencies( collectionArgumentCaptor.capture() );
        Iterator iterator = collectionArgumentCaptor.getValue().iterator();
        assertEquals( "groupID1:artifactID1:version1", iterator.next() );
        assertEquals( "groupID2:artifactID2:version2", iterator.next() );
        assertFalse( iterator.hasNext() );

        ArgumentCaptor<GAV> gavArgumentCaptor = ArgumentCaptor.forClass( GAV.class );
        when( dependencyService.loadPackageNamesForDependency( gavArgumentCaptor.capture() ) ).thenReturn( new HashSet<String>() );
        whiteListPresenter.onDependencySelected( "groupID2:artifactID2:version2" );

        verify( view ).showPackageNamesFor( gavArgumentCaptor.capture() );

        assertEquals( "artifactID2", gavArgumentCaptor.getValue().getArtifactId() );
        assertEquals( "groupID2", gavArgumentCaptor.getValue().getGroupId() );
        assertEquals( "version2", gavArgumentCaptor.getValue().getVersion() );
    }
}