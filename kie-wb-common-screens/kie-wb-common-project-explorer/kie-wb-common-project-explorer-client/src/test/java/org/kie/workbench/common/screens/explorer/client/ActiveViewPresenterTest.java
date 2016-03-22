package org.kie.workbench.common.screens.explorer.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.options.ProjectExplorerOptionsContext;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class ActiveViewPresenterTest {

    @Mock
    private BusinessViewPresenter businessViewPresenter;

    @Mock
    private TechnicalViewPresenter technicalViewPresenter;

    private ActiveViewPresenter activeViewPresenter;

    @Before
    public void setUp() throws Exception {
        activeViewPresenter = new ActiveViewPresenter( businessViewPresenter,
                                                       technicalViewPresenter );
    }

    @Test
    public void testInitBothViews() throws Exception {
        final ProjectExplorerOptionsContext activeOptionsContext = mock( ProjectExplorerOptionsContext.class );
        activeViewPresenter.init( null,
                                  activeOptionsContext );
        verify( businessViewPresenter ).init( activeOptionsContext );
        verify( technicalViewPresenter ).init( activeOptionsContext );
    }

    @Test
    public void testSetDefaultVisibilityForViews() throws Exception {
        activeViewPresenter.init( "",
                                  mock( ProjectExplorerOptionsContext.class ) );
        verify( businessViewPresenter ).setVisible( true );
        verify( technicalViewPresenter ).setVisible( false );

    }

    @Test
    public void testUpdateWhenInitializing() throws Exception {

        when( technicalViewPresenter.isVisible() ).thenReturn( true );

        activeViewPresenter.init( "",
                                  mock( ProjectExplorerOptionsContext.class ) );
        verify( businessViewPresenter ).update();
        verify( technicalViewPresenter, never() ).update();
    }

    @Test
    public void test_initialiseViewForActiveContextPathNull() throws Exception {
        activeViewPresenter.init( null,
                                  mock( ProjectExplorerOptionsContext.class ) );
        verify( businessViewPresenter ).initialiseViewForActiveContext();
        verify( technicalViewPresenter ).initialiseViewForActiveContext();
    }

    @Test
    public void test_initialiseViewForActiveContext() throws Exception {
        activeViewPresenter.init( "path",
                                  mock( ProjectExplorerOptionsContext.class ) );
        verify( businessViewPresenter ).initialiseViewForActiveContext( "path" );
        verify( technicalViewPresenter ).initialiseViewForActiveContext( "path" );
    }

    @Test
    public void testTechViewRefresh() throws Exception {
        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass( Command.class );

        when( activeOptionsContext.isTechnicalViewActive() ).thenReturn( true );

        verify( menu ).addRefreshCommand( argumentCaptor.capture() );

        argumentCaptor.getValue().execute();

        verify( technicalViewPresenter ).refresh();
        verify( businessViewPresenter, never() ).refresh();
    }

    @Test
    public void testBusinessViewRefresh() throws Exception {
        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass( Command.class );

        when( activeOptionsContext.getOptions().isBusinessViewActive() ).thenReturn( true );

        verify( menu ).addRefreshCommand( argumentCaptor.capture() );

        argumentCaptor.getValue().execute();

        verify( technicalViewPresenter, never() ).refresh();
        verify( businessViewPresenter ).refresh();
    }

    @Test
    public void testTechViewUpdate() throws Exception {
        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass( Command.class );

        when( activeOptionsContext.isTechnicalViewActive() ).thenReturn( true );

        verify( menu ).addUpdateCommand( argumentCaptor.capture() );

        argumentCaptor.getValue().execute();

        verify( technicalViewPresenter ).update();
        verify( businessViewPresenter, never() ).update();
    }

    @Test
    public void testBusinessViewUpdate() throws Exception {
        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass( Command.class );

        when( activeOptionsContext.getOptions().isBusinessViewActive() ).thenReturn( true );

        verify( menu ).addUpdateCommand( argumentCaptor.capture() );

        argumentCaptor.getValue().execute();

        verify( technicalViewPresenter, never() ).update();
        verify( businessViewPresenter ).update();
    }
}