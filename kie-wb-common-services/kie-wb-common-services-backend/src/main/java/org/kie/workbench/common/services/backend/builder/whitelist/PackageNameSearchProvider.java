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

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.POM;
import org.kie.scanner.DependencyDescriptor;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.Builder;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.backend.builder.NoBuilderFoundException;

import static org.kie.workbench.common.services.backend.dependencies.DependencyTestUtils.*;

public class PackageNameSearchProvider {

    private LRUBuilderCache builderCache;

    public PackageNameSearchProvider() {

    }

    @Inject
    public PackageNameSearchProvider( final LRUBuilderCache builderCache ) {
        this.builderCache = builderCache;
    }

    /**
     * @param pom POM for the project
     * @return All the packages that are in the direct dependencies of the pom.
     */
    public PackageNameSearch newTopLevelPackageNamesSearch( final POM pom ) {
        return new PackageNameSearch( pom );
    }

    public class PackageNameSearch {

        private final POM pom;
        private final Set<String> result = new HashSet<String>();

        private PackageNameSearch( final POM pom ) {
            this.pom = pom;
        }

        public Set<String> search()
                throws NoBuilderFoundException {
            loaPackageNames();
            return result;
        }

        private void loaPackageNames()
                throws NoBuilderFoundException {

            Builder builder = builderCache.assertBuilder( pom );

            KieModuleMetaData kieModuleMetaData = builder.getKieModuleMetaDataIgnoringErrors();

            kieModuleMetaData.getDependencies();

            for (DependencyDescriptor dependency : kieModuleMetaData.getDependencies()) {
                if ( containsDependency( toDependency( dependency ) ) ) {
                    result.addAll( kieModuleMetaData.getPackages( dependency ) );
                }
            }
        }

        private boolean containsDependency( final Dependency other ) {
            for (Dependency dependency : pom.getDependencies()) {
                if ( areEqual( dependency.getArtifactId(), other.getArtifactId() )
                        && areEqual( dependency.getGroupId(), other.getGroupId() ) ) {
                    return true;
                }
            }
            return false;
        }

        private boolean areEqual( final String value,
                                  final String other ) {
            if ( value == null || other == null ) {
                return false;
            } else {
                return value.equals( other );
            }
        }
    }
}
