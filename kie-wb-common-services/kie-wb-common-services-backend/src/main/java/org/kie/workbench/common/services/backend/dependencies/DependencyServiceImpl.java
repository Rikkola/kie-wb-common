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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.scanner.DependencyDescriptor;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.Builder;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;

@Service
@ApplicationScoped
public class DependencyServiceImpl
        implements DependencyService {

    private LRUBuilderCache builderCache;

    public DependencyServiceImpl() {
    }

    @Inject
    public DependencyServiceImpl( final LRUBuilderCache builderCache ) {
        this.builderCache = builderCache;
    }

    @Override
    public Collection<Dependency> loadDependencies( final POM pom ) {
        try {
            Builder builder = builderCache.assertBuilder( pom );
            KieModuleMetaData kieModuleMetaDataIgnoringErrors = builder.getKieModuleMetaDataIgnoringErrors();
            return toDependencies( kieModuleMetaDataIgnoringErrors.getDependencies() );
        } catch (Exception e) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private Collection<Dependency> toDependencies( final Collection<DependencyDescriptor> dependencies ) {
        ArrayList<Dependency> result = new ArrayList<Dependency>();

        for (DependencyDescriptor dependencyDescriptor : dependencies) {
            result.add( toDependency( dependencyDescriptor ) );
        }

        return result;
    }

    private Dependency toDependency( final DependencyDescriptor dependencyDescriptor ) {
        return new Dependency( new GAV( dependencyDescriptor.getGroupId(),
                                        dependencyDescriptor.getArtifactId(),
                                        dependencyDescriptor.getVersion() ) );
    }

}
