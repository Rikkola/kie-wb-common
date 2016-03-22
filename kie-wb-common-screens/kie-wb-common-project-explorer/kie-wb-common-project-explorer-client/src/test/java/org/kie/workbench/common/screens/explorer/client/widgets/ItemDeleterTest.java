package org.kie.workbench.common.screens.explorer.client.widgets;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.itemactions.ItemDeleter;
import org.kie.workbench.common.screens.explorer.client.widgets.itemactions.ItemDeleterView;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ItemDeleterTest {

    @GwtMock
    CommonConstants commonConstants;

    @Mock
    private ItemDeleterView view;

    @Mock
    private ExplorerService explorerService;

    @Mock
    private NotificationEvent notificationEvent;

    @Spy
    private EventSourceMock<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    @Captor
    private ArgumentCaptor<ParameterizedCommand> parameterizedCommandArgumentCaptor;

    private ItemDeleter itemDeleter;

    @Before
    public void setUp() throws Exception {
        itemDeleter = new ItemDeleter( view,
                                       new CallerMock<ExplorerService>( explorerService ),
                                       notification );
    }

    @Test
    public void testDelete() {

        final FolderItem item = mock( FolderItem.class );

        final Command refreshCommand = mock( Command.class );
        itemDeleter.delete( item,
                            refreshCommand );

        verifyDeletePopupShowsUp();

        userClicksOkInRemovePopup();

        verify( explorerService ).deleteItem( eq( item ),
                                              eq( "Delete comment." ) );

        verify( notification,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
        verify( refreshCommand ).execute();
    }

    private void userClicksOkInRemovePopup() {
        parameterizedCommandArgumentCaptor.getValue().execute( "Delete comment." );
    }

    private void verifyDeletePopupShowsUp() {
        verify( view ).deleteItem( parameterizedCommandArgumentCaptor.capture() );
    }


}