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
package org.kie.workbench.common.services.shared.dependencies;

import java.util.Collection;
import java.util.Set;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface DependencyService {

    Collection<Dependency> loadDependencies( final Collection<GAV> gavs );

    Collection<Dependency> loadDependencies( final GAV gav);

    Set<String> loadPackageNamesForDependency( final GAV gav );

}
