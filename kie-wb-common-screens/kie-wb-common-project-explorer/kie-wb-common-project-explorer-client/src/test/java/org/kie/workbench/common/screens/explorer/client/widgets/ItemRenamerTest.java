package org.kie.workbench.common.screens.explorer.client.widgets;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.itemactions.ItemRenamer;
import org.kie.workbench.common.screens.explorer.client.widgets.itemactions.ItemRenamerView;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopupView;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ItemRenamerTest {

    @Mock
    private ItemRenamerView view;

    @Mock
    private ExplorerService explorerService;

    @Mock
    private ValidationService validationService;

    @Mock
    private RenamePopupView renamePopupView;

    @Spy
    private Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    @Captor
    private ArgumentCaptor<CommandWithFileNameAndCommitMessage> commandWithFileNameAndCommitMessageArgumentCaptor;

    @Captor
    private ArgumentCaptor<Validator> validatorArgumentCaptor;

    private ItemRenamer renamer;

    @Before
    public void setUp() throws Exception {
        renamer = new ItemRenamer( view,
                                   renamePopupView,
                                   new CallerMock<ExplorerService>( explorerService ),
                                   new CallerMock<ValidationService>( validationService ),
                                   notification );
    }

    @Test
    public void testRenameNotification() {

        final FolderItem item = mock( FolderItem.class );

        final Path path = mock( Path.class );
        renamer.rename( path,
                        item,
                        mock( Command.class ) );

        verify( view ).renameItem( path,
                                   validatorArgumentCaptor.capture(),
                                   commandWithFileNameAndCommitMessageArgumentCaptor.capture(),
                                   renamePopupView );

        verify( notification,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
    }
}