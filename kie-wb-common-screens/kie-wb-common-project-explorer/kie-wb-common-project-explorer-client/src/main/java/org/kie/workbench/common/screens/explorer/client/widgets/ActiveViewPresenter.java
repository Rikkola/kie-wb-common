package org.kie.workbench.common.screens.explorer.client.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsContext;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewPresenter;

@Dependent
public class ActiveViewPresenter {

    private BusinessViewPresenter  businessViewPresenter;
    private TechnicalViewPresenter technicalViewPresenter;

    @Inject
    public ActiveViewPresenter( final BusinessViewPresenter businessViewPresenter,
                                final TechnicalViewPresenter technicalViewPresenter ) {
        this.businessViewPresenter = businessViewPresenter;
        this.technicalViewPresenter = technicalViewPresenter;
    }

    private void initialiseViewForActiveContext() {
        technicalViewPresenter.initialiseViewForActiveContext();
        businessViewPresenter.initialiseViewForActiveContext();
    }

    private void initialiseViewForActiveContext( final String initPath ) {
        technicalViewPresenter.initialiseViewForActiveContext( initPath );
        businessViewPresenter.initialiseViewForActiveContext( initPath );
    }

    public void init( final String initPath,
                      final ProjectExplorerOptionsContext activeOptionsContext ) {


        getActiveView().setVisible( true );
        getInactiveView().setVisible( false );

        initViews( initPath,
                   activeOptionsContext );

        update();
    }

    private void initViews( final String initPath,
                            final ProjectExplorerOptionsContext activeOptionsContext ) {
        technicalViewPresenter.init( activeOptionsContext );
        businessViewPresenter.init( activeOptionsContext );

        if ( initPath == null ) {
            initialiseViewForActiveContext();
        } else {
            initialiseViewForActiveContext( initPath );
        }
    }

    public void init() {
        getActiveView().refresh();
    }

    public void update() {
        getActiveView().update();
    }

    private BaseViewPresenter getActiveView() {
        if ( technicalViewPresenter.isVisible() ) {
            return technicalViewPresenter;
        } else {
            return businessViewPresenter;
        }
    }

    private BaseViewPresenter getInactiveView() {
        if ( technicalViewPresenter.isVisible() ) {
            return businessViewPresenter;
        } else {
            return technicalViewPresenter;
        }
    }
}
