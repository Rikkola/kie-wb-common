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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.services.backend.file.AntPathMatcher;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;

/**
 * Represents a "white list" of permitted package names for use with authoring
 */
public class PackageNameWhiteListService {

    private static final Logger logger = LoggerFactory.getLogger( PackageNameWhiteListService.class );

    private IOService ioService;
    private DependencyService dependencyService;

    @Inject
    public PackageNameWhiteListService( final @Named("ioStrategy") IOService ioService,
                                        final DependencyService dependencyService ) {
        this.ioService = ioService;
        this.dependencyService = dependencyService;
    }

    /**
     * Filter the provided Package names by the Project's white list
     * @param project Project for which to filter Package names
     * @param packageNames All Package names in the Project
     * @return A filtered collection of Package names
     */
    public Set<String> filterPackageNames( final Project project,
                                           final Collection<String> packageNames ) {
        if ( packageNames == null ) {
            return Collections.EMPTY_SET;
        } else if ( project instanceof KieProject ) {

            dependencyService.loadTopLevelDependencies( project.getPom() );

            return new PackageNameWhiteListProvider( packageNames,
                                                     getPatterns( project ) ).getFilteredPackageNames();
        } else {
            return new HashSet<String>( packageNames );
        }
    }

    private List<String> getPatterns( final Project project ) {
        final String content = readPackageNameWhiteList( (KieProject) project );

        if ( isEmpty( content ) ) {
            return new ArrayList<String>();
        } else {
            final List<String> patterns = parsePackageNamePatterns( content );

            //Convert to Paths as we're delegating to an Ant-style pattern matcher.
            //Convert once outside of the nested loops for performance reasons.
            for (int i = 0; i < patterns.size(); i++) {
                patterns.set( i,
                              patterns.get( i ).replaceAll( "\\.",
                                                            AntPathMatcher.DEFAULT_PATH_SEPARATOR ) );
            }
            return patterns;
        }
    }

    protected String readPackageNameWhiteList( final KieProject project ) {
        final org.uberfire.java.nio.file.Path packageNamesWhiteListPath = Paths.convert( project.getPackageNamesWhiteList() );
        if ( Files.exists( packageNamesWhiteListPath ) ) {
            return ioService.readAllString( packageNamesWhiteListPath );
        } else {
            return "";
        }
    }

    //See https://bugzilla.redhat.com/show_bug.cgi?id=1205180. Use OS-independent line splitting.
    private List<String> parsePackageNamePatterns( final String content ) {
        try {
            return IOUtils.readLines( new StringReader( content ) );

        } catch (IOException ioe) {
            logger.warn( "Unable to parse package names from '" + content + "'. Falling back to empty list." );
            return Collections.emptyList();
        }
    }

    private boolean isEmpty( final String content ) {
        return (content == null || content.trim().isEmpty());
    }
}

