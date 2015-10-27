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

package org.kie.workbench.common.screens.projecteditor.client.wizard;

import java.io.IOException;
import java.util.Properties;

import org.apache.maven.model.Plugin;
import org.guvnor.common.services.project.model.POM;

/**
 * The Project Name is used to generate the folder name and hence is only checked to be a valid file name.
 * The ArtifactID is initially set to the project name, subsequently validated against the maven regex,
 * and preserved as is in the pom.xml file. However, as it is used to construct the default workspace and
 * hence package names, it is sanitized in the ProjectService.newProject() method.
 */
public class POMBuilder {

    private static final String KIE_PLUGIN_VERSION_FILENAME = "/kie-plugin-version.properties";
    private static final String KIE_PLUGIN_VERSION_PROPERTY_NAME = "kie_plugin_version";

    private static final String KIE_MAVEN_PLUGIN_GROUP_ID = "org.kie";
    private static final String KIE_MAVEN_PLUGIN_ARTIFACT_ID = "kie-maven-plugin";
    private static final String KIE_MAVEN_PLUGIN_VERSION = getKiePluginVersion();

    private static final Plugin KIE_MAVEN_PLUGIN = getKieMavenPlugin();

    private POM pom;

    public POMBuilder() {
        this.pom = new POM();
        pom.getGav().setVersion( "1.0" );
    }

    public POMBuilder setProjectName( String projectName ) {
        pom.setName( projectName );
        if ( projectName != null ) {
            pom.getGav().setArtifactId( sanitizeProjectName( projectName ) );
        }
        return this;
    }

    public POMBuilder setGroupId( String groupId ) {
        pom.getGav().setGroupId( groupId );
        return this;
    }

    public POMBuilder setVersion( String version ) {
        pom.getGav().setVersion( version );
        return this;
    }

    public POMBuilder setMultiModule( boolean isMultiModule ) {
        pom.setMultiModule( isMultiModule );
        return this;
    }

    POM build() {
        return pom;
    }

    //The projectName has been validated as a FileSystem folder name, which may not be consistent with Maven ArtifactID
    //naming restrictions (see org.apache.maven.model.validation.DefaultModelValidator.java::ID_REGEX). Therefore we'd
    //best sanitize the projectName
    private String sanitizeProjectName( final String projectName ) {
        //Only [A-Za-z0-9_\-.] are valid so strip everything else out
        return projectName != null ? projectName.replaceAll( "[^A-Za-z0-9_\\-.]", "" ) : projectName;
    }

    private static Plugin getKieMavenPlugin() {
        final Plugin plugin = new Plugin();
        plugin.setGroupId( KIE_MAVEN_PLUGIN_GROUP_ID );
        plugin.setArtifactId( KIE_MAVEN_PLUGIN_ARTIFACT_ID );
        plugin.setVersion( KIE_MAVEN_PLUGIN_VERSION );
        plugin.setExtensions(true);
        return plugin;
    }

    //Used by tests; hence public accessor
    public static String getKiePluginVersion() {
        Properties p = new Properties();
        try {
            p.load(POMContentHandler.class.getResourceAsStream(KIE_PLUGIN_VERSION_FILENAME));
        } catch (IOException e) {

        }
        return p.getProperty(KIE_PLUGIN_VERSION_PROPERTY_NAME);
    }
}
