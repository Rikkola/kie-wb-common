package org.kie.workbench.common.screens.explorer.client.widgets.options;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerOptions;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mvp.PlaceRequest;

@Dependent
public class ProjectExplorerOptionsBuilder {

    private ProjectExplorerOptions options = new ProjectExplorerOptions();
    private PlaceRequest                 placeRequest;
    private Map<String, List<String>>    parameterMap;
    private ProjectExplorerOptionsLoader activeOptionsLoader;

    @Inject
    public ProjectExplorerOptionsBuilder( final ProjectExplorerOptionsLoader activeOptionsLoader ) {
        this.activeOptionsLoader = activeOptionsLoader;
    }

    public ProjectExplorerOptions build( final PlaceRequest placeRequest,
                                         final Map<String, List<String>> parameterMap ) {

        this.placeRequest = placeRequest;
        this.parameterMap = parameterMap;

        final Set<Option> optionsFromModeParameter = getOptionsFromModeParameter( placeRequest.getParameter( "mode", "" ) );

        if ( optionsFromModeParameter.isEmpty() ) {
            options.addAll( getOptionsFromModeParameter( getWindowParameter( "explorer_mode" ) ) );
        } else {
            options.addAll( optionsFromModeParameter );
        }

        if ( isContextNavigationOff() ) {
            options.add( Option.NO_CONTEXT_NAVIGATION );
        }
        return options;
    }

    private boolean isContextNavigationOff() {
        final boolean noContextNavigationOption = doWindowParametersContain( "no_context_navigation" );
        final boolean noContext = placeRequest.getParameterNames().contains( "no_context" );

        return noContext || noContextNavigationOption;
    }

    private String getWindowParameter( final String parameterName ) {
        if ( doWindowParametersContain( parameterName ) ) {
            return parameterMap.get( parameterName ).get( 0 ).trim();
        } else {
            return "";
        }
    }

    private boolean doWindowParametersContain( final String parameterName ) {
        return parameterMap.containsKey( parameterName );
    }

    private Set<Option> getOptionsFromModeParameter( final String explorerMode ) {
        Set<Option> result = new HashSet<Option>();

        if ( explorerMode == null ) {
            return result;
        } else if ( explorerMode.equalsIgnoreCase( "business_tree" ) ) {
            result.add( Option.BUSINESS_CONTENT );
            result.add( Option.TREE_NAVIGATOR );
        } else if ( explorerMode.equalsIgnoreCase( "business_explorer" ) ) {
            result.add( Option.BUSINESS_CONTENT );
            result.add( Option.BREADCRUMB_NAVIGATOR );
        } else if ( explorerMode.equalsIgnoreCase( "tech_tree" ) ) {
            result.add( Option.TECHNICAL_CONTENT );
            result.add( Option.TREE_NAVIGATOR );
        } else if ( explorerMode.equalsIgnoreCase( "tech_explorer" ) ) {
            result.add( Option.TECHNICAL_CONTENT );
            result.add( Option.BREADCRUMB_NAVIGATOR );
        }

        return result;
    }

    public void build( final PlaceRequest placeRequest,
                       final Map<String, List<String>> parameterMap,
                       final Callback<ProjectExplorerOptionsContext> callback ) {

        final ProjectExplorerOptions projectExplorerOptions = build( placeRequest,
                                                                     parameterMap );

        if ( projectExplorerOptions.isEmpty() ) {
            activeOptionsLoader.load( new Callback<ProjectExplorerOptions>() {
                @Override
                public void callback( final ProjectExplorerOptions result ) {
                    callback.callback( new ProjectExplorerOptionsContext( projectExplorerOptions ) );
                }
            } );
        } else {
            callback.callback( new ProjectExplorerOptionsContext( projectExplorerOptions ) );
        }
    }
}
