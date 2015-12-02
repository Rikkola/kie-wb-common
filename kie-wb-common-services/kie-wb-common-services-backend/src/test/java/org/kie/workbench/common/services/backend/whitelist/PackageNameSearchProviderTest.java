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
package org.kie.workbench.common.services.backend.whitelist;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.DependencyDescriptor;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.Builder;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.backend.builder.NoBuilderFoundException;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PackageNameSearchProviderTest {

    @Mock
    private LRUBuilderCache lruBuilderCache;

    private PackageNameSearchProvider packageNameSearchProvider;

    @Before
    public void setUp() throws Exception {
        packageNameSearchProvider = new PackageNameSearchProvider( lruBuilderCache );
    }

    private HashMap<DependencyDescriptor, Set<String>> setUp( POM pom ) throws NoBuilderFoundException {
        final HashMap<DependencyDescriptor, Set<String>> map = new HashMap<DependencyDescriptor, Set<String>>();

        Builder builder = mock( Builder.class );
        when( lruBuilderCache.assertBuilder( pom ) ).thenReturn( builder );
        KieModuleMetaData kieModuleMetaData = mock( KieModuleMetaData.class );
        when( builder.getKieModuleMetaDataIgnoringErrors() ).thenReturn( kieModuleMetaData );
        when( kieModuleMetaData.getDependencies() ).thenReturn( map.keySet() );
        when( kieModuleMetaData.getPackages( any( DependencyDescriptor.class ) ) ).thenAnswer( new Answer<Set>() {
            @Override
            public Set answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return map.get( invocationOnMock.getArguments()[0] );
            }
        } );
        return map;
    }

    @Test
    public void testLoadTopLevelDependencies() throws Exception {
        POM pom = new POM( new GAV( "artifactID",
                                    "groupID",
                                    "version" ) );
        pom.getDependencies().add( getDependency( "drools-core", "org.drools", "6.3.0" ) );
        pom.getDependencies().add( getDependency( "junit", "org.junit", "4.11" ) );

        HashMap<DependencyDescriptor, Set<String>> map = setUp( pom );

        map.put( getDependencyDescriptor( "drools-core", "org.drools", "6.3.0" ), toSet( "org.drools.a",
                                                                                         "org.drools.b",
                                                                                         "org.drools.c" ) );
        map.put( getDependencyDescriptor( "junit", "org.junit", "4.11" ), toSet( "junit.a",
                                                                                 "junit.b" ) );

        Set<String> packageNames = packageNameSearchProvider.newTopLevelPackageNamesSearch( pom ).search();

        assertEquals( 5, packageNames.size() );
        assertTrue( packageNames.contains( "org.drools.a" ) );
        assertTrue( packageNames.contains( "org.drools.b" ) );
        assertTrue( packageNames.contains( "org.drools.c" ) );
        assertTrue( packageNames.contains( "junit.a" ) );
        assertTrue( packageNames.contains( "junit.b" ) );
    }

    @Test
    public void testLoadTopLevelDependenciesWhenIncompleteDependenciesInPOM() throws Exception {
        POM pom = new POM( new GAV( "artifactID",
                                    "groupID",
                                    "version" ) );
        pom.getDependencies().add( getDependency( "drools-core", "org.drools", null ) );
        pom.getDependencies().add( getDependency( null, null, null ) );

        HashMap<DependencyDescriptor, Set<String>> map = setUp( pom );
        map.put( getDependencyDescriptor( "drools-core", "org.drools", "6.3.0" ), toSet( "org.drools.a",
                                                                                         "org.drools.b",
                                                                                         "org.drools.c" ) );

        Set<String> packageNames = packageNameSearchProvider.newTopLevelPackageNamesSearch( pom ).search();

        assertEquals( 3, packageNames.size() );
        assertTrue( packageNames.contains( "org.drools.a" ) );
        assertTrue( packageNames.contains( "org.drools.b" ) );
        assertTrue( packageNames.contains( "org.drools.c" ) );
    }

    private HashSet<String> toSet( String... items ) {
        return new HashSet<String>( Arrays.asList( items ) );
    }

    private DependencyDescriptor getDependencyDescriptor( final String artifactID,
                                                          final String groupID,
                                                          final String version ) {
        return new DependencyDescriptor( new ReleaseId() {
            @Override public String getGroupId() {
                return groupID;
            }

            @Override public String getArtifactId() {
                return artifactID;
            }

            @Override public String getVersion() {
                return version;
            }

            @Override public String toExternalForm() {
                return null;
            }

            @Override public boolean isSnapshot() {
                return false;
            }
        } );
    }


    private Dependency getDependency( final String artifactID, final String groupID, final String version ) {
        return new Dependency( new GAV( groupID, artifactID, version ) );
    }

}