/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.services.backend.file.AntPathMatcher;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class PackageNameWhiteListProvider {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private final Collection<String> packageNames;
    private final List<String> patterns;

    public PackageNameWhiteListProvider( final Collection<String> packageNames,
                                         final List<String> patterns ) {

        this.packageNames = checkNotNull( "packageNames", packageNames );
        this.patterns = checkNotNull( "patterns", patterns );
    }

    public Set<String> getFilteredPackageNames() {
        if ( patterns.isEmpty() ) {
            return new HashSet<String>( packageNames );
        } else {
            return filter( getPackageNamePaths() );
        }
    }

    /**
     * @param packageNamePaths
     * @return Package Names matching the White List to the available packages
     */
    private Set<String> filter( HashMap<String, String> packageNamePaths ) {
        final Set<String> result = new HashSet<String>();
        for (String pattern : patterns) {
            for (Map.Entry<String, String> packageNamePath : packageNamePaths.entrySet()) {
                if ( ANT_PATH_MATCHER.match( pattern,
                                             packageNamePath.getValue() ) ) {
                    result.add( packageNamePath.getKey() );
                }
            }
        }

        return result;
    }

    /**
     * Fetching the paths to a map to avoid loops inside loops
     * @return
     */
    private HashMap<String, String> getPackageNamePaths() {
        final HashMap<String, String> packageNamePaths = new HashMap<String, String>();
        for (String packageName : packageNames) {
            packageNamePaths.put( packageName,
                                  packageName.replaceAll( "\\.",
                                                          AntPathMatcher.DEFAULT_PATH_SEPARATOR ) );
        }
        return packageNamePaths;
    }

}
