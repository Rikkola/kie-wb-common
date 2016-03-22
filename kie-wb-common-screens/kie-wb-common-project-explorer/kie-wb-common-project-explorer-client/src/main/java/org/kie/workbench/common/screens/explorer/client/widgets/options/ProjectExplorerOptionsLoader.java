package org.kie.workbench.common.screens.explorer.client.widgets.options;

import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerOptions;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.uberfire.client.callbacks.Callback;

@Dependent
public class ProjectExplorerOptionsLoader {

    private Caller<ExplorerService>          explorerService;
    private Callback<ProjectExplorerOptions> activeOptionsCallback;

    @Inject
    public ProjectExplorerOptionsLoader( final Caller<ExplorerService> explorerService ) {
        this.explorerService = explorerService;
    }

    public void load( final Callback<ProjectExplorerOptions> activeOptionsCallback ) {

        this.activeOptionsCallback = activeOptionsCallback;

        explorerService.call( getLoadSuccessCallback(),
                              getLoadErrorCallback() ).getLastUserOptions();
    }

    private RemoteCallback<Set<Option>> getLoadSuccessCallback() {
        return new RemoteCallback<Set<Option>>() {
            @Override
            public void callback( Set<Option> optionsResult ) {
                if ( isNullOrEmpty( optionsResult ) ) {
                    activeOptionsCallback.callback( build( optionsResult ) );
                } else {
                    activeOptionsCallback.callback( getDefaultOptions() );
                }
            }
        };
    }

    private ProjectExplorerOptions getDefaultOptions() {
        final ProjectExplorerOptions options = new ProjectExplorerOptions();
        options.addAll( Option.BUSINESS_CONTENT,
                        Option.BREADCRUMB_NAVIGATOR,
                        Option.EXCLUDE_HIDDEN_ITEMS );
        return options;
    }

    private ProjectExplorerOptions build( final Set<Option> optionsResult ) {
        final ProjectExplorerOptions options = new ProjectExplorerOptions();
        options.addAll( optionsResult );
        return options;
    }

    private boolean isNullOrEmpty( final Set<Option> optionsResult ) {
        return optionsResult != null && !optionsResult.isEmpty();
    }

    private ErrorCallback<Object> getLoadErrorCallback() {
        return new ErrorCallback<Object>() {
            @Override
            public boolean error( Object o,
                                  Throwable throwable ) {
                activeOptionsCallback.callback( getDefaultOptions() );
                return false;
            }
        };
    }

}
