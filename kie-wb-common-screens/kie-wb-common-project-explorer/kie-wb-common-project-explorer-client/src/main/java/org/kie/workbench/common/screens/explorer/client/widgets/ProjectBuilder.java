package org.kie.workbench.common.screens.explorer.client.widgets;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

@Dependent
public class ProjectBuilder {

    protected Caller<BuildService> buildService;
    protected Event<BuildResults>  buildResultsEvent;

    @Inject
    public ProjectBuilder( final Caller<BuildService> buildService,
                           final Event<BuildResults> buildResultsEvent ) {
        this.buildService = buildService;
        this.buildResultsEvent = buildResultsEvent;
    }

    public void buildProject( final Project project ) {
        //Don't build automatically if disabled
        if ( ApplicationPreferences.getBooleanPref( ExplorerService.BUILD_PROJECT_PROPERTY_NAME ) ) {
            return;
        }
        if ( project == null ) {
            return;
        }
        buildService.call(
                new RemoteCallback<BuildResults>() {
                    @Override
                    public void callback( final BuildResults results ) {
                        buildResultsEvent.fire( results );
                    }
                },
                new DefaultErrorCallback() ).build( project );
    }
}
