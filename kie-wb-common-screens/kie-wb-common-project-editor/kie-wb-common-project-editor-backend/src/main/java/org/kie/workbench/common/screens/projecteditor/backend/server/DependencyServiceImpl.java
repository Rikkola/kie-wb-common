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
package org.kie.workbench.common.screens.projecteditor.backend.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.backend.server.POMContentHandler;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.scanner.DependencyDescriptor;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.screens.projecteditor.service.DependencyService;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.shared.project.KieProjectService;

@Service
@ApplicationScoped
public class DependencyServiceImpl
        implements DependencyService {

    @Inject
    private LRUBuilderCache builderCache;

    @Inject
    private KieProjectService projectService;

    @Inject
    private POMContentHandler pomContentHandler;

    @Override
    public Collection<Dependency> loadDependencies( POM pom ) {

        try {
            File tempPomXML = File.createTempFile( "pom", ".xml" );
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( tempPomXML ) );

            try {
                bufferedWriter.write( pomContentHandler.toString( pom ) );

//        final KieProject project = projectService.resolveProject( pathToPOM );
//            if ( project == null ) {
//                logger.error( "A Project could not be resolved for path '" + resource.toURI() + "'. No enums will be returned." );
//                return null;
//            }
//        final KieModule module = builderCache.assertBuilder( project ).getKieModuleIgnoringErrors();
//            if ( module == null ) {
//                logger.error( "A KieModule could not be resolved for path '" + resource.toURI() + "'. No enums will be returned." );
//                return null;
//            }
                Collection<Dependency> dependencies = toDependencies( KieModuleMetaData.Factory.newKieModuleMetaData( tempPomXML ).getDependencies() );
                return dependencies;
            } finally {
                bufferedWriter.close();
                tempPomXML.delete();
            }
        } catch (Exception e) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private Collection<Dependency> toDependencies( Collection<DependencyDescriptor> dependencies ) {
        ArrayList<Dependency> result = new ArrayList<Dependency>();

        for (DependencyDescriptor dependencyDescriptor : dependencies) {
            result.add( toDependency( dependencyDescriptor ) );
        }

        return result;
    }

    private Dependency toDependency( DependencyDescriptor dependencyDescriptor ) {
        return new Dependency( new GAV( dependencyDescriptor.getGroupId(),
                                        dependencyDescriptor.getArtifactId(),
                                        dependencyDescriptor.getVersion() ) );
    }

}
