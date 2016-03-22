package org.kie.workbench.common.screens.explorer.client.widgets;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.itemactions.ItemCopier;
import org.kie.workbench.common.screens.explorer.client.widgets.itemactions.ItemCopierView;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.CopyPopupView;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ItemCopierTest {

    @GwtMock
    CommonConstants commonConstants;

    @Mock
    ValidationService validationService;

    @Mock
    private ItemCopierView view;

    @Mock
    private CopyPopupView copyPopupView;

    @Mock
    private ExplorerService explorerService;

    @Spy
    private Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    @Captor
    private ArgumentCaptor<CommandWithFileNameAndCommitMessage> commandWithFileNameAndCommitMessageArgumentCaptor;

    @Captor
    private ArgumentCaptor<Validator> validatorArgumentCaptor;

    private ItemCopier itemCopier;

    @Before
    public void setUp() throws Exception {
        itemCopier = new ItemCopier( view,
                                     copyPopupView,
                                     new CallerMock<ExplorerService>( explorerService ),
                                     new CallerMock<ValidationService>( validationService ),
                                     notification );
    }


    @Test
    public void testCopyNotification() {

        final FolderItem item = mock( FolderItem.class );

        final Path path = mock( Path.class );
        final Command refreshCommand = mock( Command.class );
        itemCopier.copy( item,
                         path,
                         refreshCommand );

        verify( view ).copyItem( path,
                                 validatorArgumentCaptor.capture(),
                                 commandWithFileNameAndCommitMessageArgumentCaptor.capture(),
                                 copyPopupView );

        verify( notification,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
    }

}