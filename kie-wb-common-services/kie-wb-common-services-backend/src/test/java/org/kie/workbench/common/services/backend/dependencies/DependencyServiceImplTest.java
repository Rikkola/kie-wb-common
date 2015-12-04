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
package org.kie.workbench.common.services.backend.dependencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DependencyServiceImplTest {


    private DependencyServiceImpl service;

    @Before
    public void setUp() throws Exception {
        service = new DependencyServiceImpl();
    }

    @Test
    public void testNoDependencies() throws Exception {

        GAV gav = new GAV( "artifactID",
                           "groupID",
                           "version" );

        Collection<Dependency> dependencies = service.loadDependencies( gav );

        assertTrue( dependencies.isEmpty() );
    }

    @Test
    public void testDependencies() throws Exception {
        GAV gav = new GAV( "junit",
                           "junit",
                           "4.11" );

        Collection<Dependency> dependencies = service.loadDependencies( gav );

        assertEquals( 1, dependencies.size() );
        assertContainsDependency( dependencies, "org.hamcrest", "hamcrest-core", "1.3" );
    }

    @Test
    public void testDependenciesList() throws Exception {
        ArrayList<GAV> gavs = new ArrayList<GAV>();
        gavs.add( new GAV( "junit",
                           "junit",
                           "4.11" ) );
        // Twice to make sure we do not return duplicate items
        gavs.add( new GAV( "junit",
                           "junit",
                           "4.11" ) );
        gavs.add( new GAV( "org.mockito",
                           "mockito-core",
                           "1.9.5" ) );

        Collection<Dependency> dependencies = service.loadDependencies( gavs );

        assertEquals( 3, dependencies.size() );
        assertContainsDependency( dependencies, "org.hamcrest", "hamcrest-core", "1.3" );
        assertContainsDependency( dependencies, "org.hamcrest", "hamcrest-core", "1.1" );
        assertContainsDependency( dependencies, "org.objenesis", "objenesis", "1.0" );
    }

    @Test
    public void testListPackages() throws Exception {

        Set<String> junitPackages = service.loadPackageNamesForDependency( new GAV( "junit",
                                                                                    "junit",
                                                                                    "4.11" ) );
        Set<String> hamcrestPackages = service.loadPackageNamesForDependency( new GAV( "org.hamcrest",
                                                                                       "hamcrest-core",
                                                                                       "1.3" ) );

        assertTrue( junitPackages.contains( "org.junit.rules" ) );
        assertTrue( junitPackages.contains( "org.junit.matchers" ) );

        assertFalse( junitPackages.contains( "org.hamcrest" ) );
        assertFalse( junitPackages.contains( "org.hamcrest.core" ) );

        assertTrue( hamcrestPackages.contains( "org.hamcrest" ) );
        assertTrue( hamcrestPackages.contains( "org.hamcrest.core" ) );

        assertFalse( hamcrestPackages.contains( "org.junit.rules" ) );
        assertFalse( hamcrestPackages.contains( "org.junit.matchers" ) );

    }

    private void assertContainsDependency( final Collection<Dependency> dependencies,
                                           final String groupID,
                                           final String artifactID,
                                           final String version ) {
        boolean foundIt = false;
        for (Dependency dependency : dependencies) {
            if ( groupID.equals( dependency.getGroupId() )
                    && artifactID.equals( dependency.getArtifactId() )
                    && version.equals( dependency.getVersion() ) ) {
                foundIt = true;
                break;
            }
        }
        assertTrue( foundIt );
    }
}