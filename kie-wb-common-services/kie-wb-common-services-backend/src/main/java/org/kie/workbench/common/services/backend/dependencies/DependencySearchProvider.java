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
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.POM;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;

public class DependencySearchProvider {

    private DependencyService dependencyService;

    public DependencySearchProvider() {

    }

    @Inject
    public DependencySearchProvider( final DependencyService dependencyService ) {
        this.dependencyService = dependencyService;
    }

    public TopLevelDependencySearch newTopLevelDependencySearch( final POM pom ) {
        return new TopLevelDependencySearch( pom );
    }

    class TopLevelDependencySearch {

        private final POM pom;
        private Collection<Dependency> dependencies = null;
        private final ArrayList<Dependency> result = new ArrayList<Dependency>();

        private TopLevelDependencySearch( final POM pom ) {
            this.pom = pom;
        }

        public ArrayList<Dependency> search() {
            for (Dependency dependency : pom.getDependencies()) {
                if ( isGAVDefined( dependency ) ) {
                    result.add( dependency );
                } else {
                    Dependency foundDependency = search( dependency );
                    if ( foundDependency != null ) {
                        result.add( foundDependency );
                    }
                }
            }
            return result;
        }

        private void loadDependencies() {
            if ( dependencies == null ) {
                dependencies = dependencyService.loadDependencies( pom );
            }
        }

        private Dependency search( final Dependency dependency ) {
            loadDependencies();
            for (Dependency other : dependencies) {
                if ( areEqual( dependency.getGroupId(),
                               other.getGroupId() )
                        && areEqual( dependency.getArtifactId(),
                                     other.getArtifactId() ) ) {
                    return other;
                }
            }
            return null;
        }

        private boolean areEqual( final String value,
                                  final String other ) {
            if ( value == null || other == null ) {
                return false;
            } else {
                return value.equals( other );
            }
        }

        private boolean isGAVDefined( final Dependency dependency ) {
            return !isEmpty( dependency.getGroupId() )
                    && !isEmpty( dependency.getArtifactId() )
                    && !isEmpty( dependency.getVersion() );
        }

        private boolean isEmpty( final String value ) {
            return value == null || value.trim().isEmpty();
        }
    }
}
