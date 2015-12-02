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
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.scanner.MavenRepository;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;

import static org.kie.workbench.common.services.backend.dependencies.DependencyTestUtils.*;

@Service
@ApplicationScoped
public class DependencyServiceImpl
        implements DependencyService {

//    private LRUBuilderCache builderCache;

    public DependencyServiceImpl() {
    }

//    @Inject
//    public DependencyServiceImpl( final LRUBuilderCache builderCache ) {
//        this.builderCache = builderCache;
//    }

    @Override
    public Collection<Dependency> loadDependencies( final GAV gav ) {

        MavenRepository mavenRepository = MavenRepository.getMavenRepository();

        return toDependencies( mavenRepository.getArtifactDependecies( gav.toString() ) );

//        try {
//            Builder builder = builderCache.assertBuilder( pom );
//            KieModuleMetaData kieModuleMetaDataIgnoringErrors = builder.getKieModuleMetaDataIgnoringErrors();
//            return toDependencies( kieModuleMetaDataIgnoringErrors.getDependencies() );
//        } catch (Exception e) {
//            throw ExceptionUtilities.handleException( e );
//        }
    }

    @Override public Set<String> loadPackageNamesForDependency( GAV gav ) {
        return new HashSet<String>();
    }

}
