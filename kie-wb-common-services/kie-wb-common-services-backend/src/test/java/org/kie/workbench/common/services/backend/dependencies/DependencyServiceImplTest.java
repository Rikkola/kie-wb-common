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
import org.kie.workbench.common.services.backend.builder.NoBuilderFoundException;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

        mockMetaData( new ArrayList<DependencyDescriptor>() );

        Collection<Dependency> dependencies = service.loadDependencies( gav );

        assertTrue( dependencies.isEmpty() );
    }

    @Test
    public void testDependencies() throws Exception {
        GAV gav = new GAV( "artifactID",
                           "groupID",
                           "version" );
        POM pom = new POM( gav );
        pom.getDependencies().add( new Dependency() );

        mockMetaData( new ArrayList<DependencyDescriptor>() );

        Collection<Dependency> dependencies = service.loadDependencies( gav );

        assertTrue( dependencies.isEmpty() );
    }

    private void mockMetaData( final ArrayList<DependencyDescriptor> allDependencies ) throws NoBuilderFoundException {
        KieModuleMetaData kieModuleMetaData = mock( KieModuleMetaData.class );
        when( kieModuleMetaData.getDependencies() ).thenReturn( allDependencies );
    }

}