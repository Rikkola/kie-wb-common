/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.kie.workbench.common.services.backend.builder.whitelist;

import java.util.ArrayList;
import java.util.Collection;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PackageNameSearchProviderTest {

    @Mock
    private DependencyService dependencyService;

    private PackageNameSearchProvider packageNameSearchProvider;

    @Before
    public void setUp() throws Exception {
        packageNameSearchProvider = new PackageNameSearchProvider( dependencyService );
    }

    @Test
    public void testLoadTopLevelDependencies() throws Exception {
        POM pom = new POM( new GAV( "artifactID",
                                    "groupID",
                                    "version" ) );
        pom.getDependencies().add( getDependency( "drools-core", "org.drools", "6.3.0" ) );
        pom.getDependencies().add( getDependency( "junit", "org.junit", null ) );

        ArrayList<Dependency> allDependencies = new ArrayList<Dependency>();
        allDependencies.add( getDependency( "drools-core", "org.drools", "6.3.0" ) );
        allDependencies.add( getDependency( "junit", "org.junit", "4.11" ) );
        allDependencies.add( getDependency( "hamcrest-core", "org.hamcrest", "1.3" ) );

        when( dependencyService.loadDependencies( pom ) ).thenReturn( allDependencies );

        Collection<Dependency> dependencies = packageNameSearchProvider.newTopLevelPackageNamesSearch( pom ).search();

        assertEquals( 2, dependencies.size() );
        Dependency droolsCore = find( "drools-core", dependencies );
        assertEquals( "org.drools", droolsCore.getGroupId() );
        assertEquals( "6.3.0", droolsCore.getVersion() );

        Dependency junit = find( "junit", dependencies );
        assertEquals( "org.junit", junit.getGroupId() );
        assertEquals( "4.11", junit.getVersion() );
    }

    @Test
    public void testLoadTopLevelDependenciesWhenIncompleteDependenciesInPOM() throws Exception {
        POM pom = new POM( new GAV( "artifactID",
                                    "groupID",
                                    "version" ) );
        pom.getDependencies().add( getDependency( "drools-core", "org.drools", null ) );
        pom.getDependencies().add( getDependency( null, null, null ) );

        ArrayList<Dependency> allDependencies = new ArrayList<Dependency>();
        allDependencies.add( getDependency( "drools-core", "org.drools", "6.3.0" ) );

        when( dependencyService.loadDependencies( pom ) ).thenReturn( allDependencies );

        Collection<Dependency> dependencies = packageNameSearchProvider.newTopLevelPackageNamesSearch( pom ).search();

        assertEquals( 1, dependencies.size() );
        Dependency droolsCore = find( "drools-core", dependencies );
        assertEquals( "org.drools", droolsCore.getGroupId() );
        assertEquals( "6.3.0", droolsCore.getVersion() );
    }

    private Dependency find( final String artifactID,
                             final Collection<Dependency> dependencies ) {
        for (Dependency dependency : dependencies) {
            if ( artifactID.equals( dependency.getArtifactId() ) ) {
                return dependency;
            }
        }
        fail( "Dependency with artifact ID: " + artifactID + " does not exist." );
        return null;
    }

    private Dependency getDependency( final String artifactID, final String groupID, final String version ) {
        return new Dependency( new GAV( groupID, artifactID, version ) );
    }

}