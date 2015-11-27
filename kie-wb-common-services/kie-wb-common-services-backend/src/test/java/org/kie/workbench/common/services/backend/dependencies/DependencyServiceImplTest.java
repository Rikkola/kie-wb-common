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

import java.util.Collection;

import org.guvnor.common.services.project.backend.server.POMContentHandler;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.backend.dependencies.DependencyServiceImpl;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

public class DependencyServiceImplTest {

    private DependencyServiceImpl service;

    @Before
    public void setUp() throws Exception {
        service = new DependencyServiceImpl( new POMContentHandler() );

    }

    @Test
    public void testNoDependencies() throws Exception {
        Collection<Dependency> dependencies = service.loadDependencies( new POM( new GAV( "artifactID",
                                                                                          "groupID",
                                                                                          "version" ) ) );

        assertTrue( dependencies.isEmpty() );
    }

    @Test
    public void testDependencies() throws Exception {
        POM pom = new POM( new GAV( "artifactID",
                                    "groupID",
                                    "version" ) );
        pom.getDependencies().add( new Dependency() );
        Collection<Dependency> dependencies = service.loadDependencies( pom );

        assertTrue( dependencies.isEmpty() );
    }
}