package org.kie.workbench.common.screens.explorer.client.widgets.itemactions;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopupView;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class ItemRenamer {

    private RenamePopupView renamePopupView;
    protected Caller<ExplorerService> explorerService;

    protected Caller<ValidationService> validationService;

    private ItemRenamerView view;

    private Event<NotificationEvent> notification;
    private Command                  refreshCommand;

    @Inject
    public ItemRenamer( final ItemRenamerView view,
                        final RenamePopupView renamePopupView,
                        final Caller<ExplorerService> explorerService,
                        final Caller<ValidationService> validationService,
                        final Event<NotificationEvent> notification ) {
        this.view = view;
        this.renamePopupView = renamePopupView;
        this.explorerService = explorerService;
        this.validationService = validationService;
        this.notification = notification;
    }

    public void rename( final Path path,
                        final FolderItem folderItem,
                        final Command refreshCommand ) {
        this.refreshCommand = refreshCommand;

        view.renameItem( path,
                         new Validator() {
                             @Override
                             public void validate( final String value,
                                                   final ValidatorCallback callback ) {
                                 validationService.call( new RemoteCallback<Object>() {
                                     @Override
                                     public void callback( Object response ) {
                                         if ( Boolean.TRUE.equals( response ) ) {
                                             callback.onSuccess();
                                         } else {
                                             callback.onFailure();
                                         }
                                     }
                                 } ).isFileNameValid( path,
                                                      value );
                             }
                         },
                         new CommandWithFileNameAndCommitMessage() {
                             @Override
                             public void execute( final FileNameAndCommitMessage details ) {
                                 view.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                 explorerService.call(
                                         getRenameSuccessCallback( renamePopupView ),
                                         getRenameErrorCallback( renamePopupView )
                                                     ).renameItem( folderItem,
                                                                   details.getNewFileName(),
                                                                   details.getCommitMessage() );
                             }
                         },
                         renamePopupView
                       );
    }

    protected RemoteCallback<Void> getRenameSuccessCallback( final RenamePopupView renamePopupView ) {
        return new RemoteCallback<Void>() {
            @Override
            public void callback( final Void o ) {
                renamePopupView.hide();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
                view.hideBusyIndicator();
                refreshCommand.execute();
            }
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getRenameErrorCallback( final RenamePopupView renamePopupView ) {
        return new HasBusyIndicatorDefaultErrorCallback( view ) {

            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                renamePopupView.hide();
                return super.error( message, throwable );
            }
        };
    }
}
