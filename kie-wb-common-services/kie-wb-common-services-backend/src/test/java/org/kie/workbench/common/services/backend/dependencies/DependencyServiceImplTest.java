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

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.scanner.DependencyDescriptor;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.Builder;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.backend.builder.NoBuilderFoundException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DependencyServiceImplTest {

    @Mock
    private LRUBuilderCache builderCache;

    private DependencyServiceImpl service;

    @Before
    public void setUp() throws Exception {
        service = new DependencyServiceImpl( builderCache );
    }

    @Test
    public void testNoDependencies() throws Exception {

        POM pom = new POM( new GAV( "artifactID",
                                    "groupID",
                                    "version" ) );

        mockMetaData( pom, new ArrayList<DependencyDescriptor>() );

        Collection<Dependency> dependencies = service.loadDependencies( pom );

        assertTrue( dependencies.isEmpty() );
    }

    @Test
    public void testDependencies() throws Exception {
        POM pom = new POM( new GAV( "artifactID",
                                    "groupID",
                                    "version" ) );
        pom.getDependencies().add( new Dependency() );

        mockMetaData( pom, new ArrayList<DependencyDescriptor>() );

        Collection<Dependency> dependencies = service.loadDependencies( pom );

        assertTrue( dependencies.isEmpty() );
    }

    private void mockMetaData( final POM pom,
                               final ArrayList<DependencyDescriptor> allDependencies ) throws NoBuilderFoundException {
        Builder builder = mock( Builder.class );
        when( builderCache.assertBuilder( pom ) ).thenReturn( builder );
        KieModuleMetaData kieModuleMetaData = mock( KieModuleMetaData.class );
        when( builder.getKieModuleMetaDataIgnoringErrors() ).thenReturn( kieModuleMetaData );
        when( kieModuleMetaData.getDependencies() ).thenReturn( allDependencies );
    }

}