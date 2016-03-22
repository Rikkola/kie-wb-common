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
import org.uberfire.ext.editor.commons.client.file.CopyPopupView;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class ItemCopier {

    protected Caller<ValidationService> validationService;
    protected Caller<ExplorerService>   explorerService;
    private   CopyPopupView             copyPopupView;
    private   ItemCopierView            view;
    private   Event<NotificationEvent>  notification;
    private   Command                   refreshCommand;

    @Inject
    public ItemCopier( final ItemCopierView view,
                       final CopyPopupView copyPopupView,
                       final Caller<ExplorerService> explorerService,
                       final Caller<ValidationService> validationService,
                       final Event<NotificationEvent> notification ) {
        this.view = view;
        this.copyPopupView = copyPopupView;
        this.explorerService = explorerService;
        this.validationService = validationService;
        this.notification = notification;
    }

    public void copy( final FolderItem folderItem,
                      final Path path,
                      final Command refreshCommand ) {
        this.refreshCommand = refreshCommand;

        view.copyItem( path,
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
                               view.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                               explorerService.call( getCopySuccessCallback( copyPopupView ),
                                                     getCopyErrorCallback( copyPopupView ) ).copyItem( folderItem,
                                                                                                       details.getNewFileName(),
                                                                                                       details.getCommitMessage() );
                           }
                       },
                       copyPopupView
                     );
    }

    protected RemoteCallback<Void> getCopySuccessCallback( final CopyPopupView copyPopupView ) {
        return new RemoteCallback<Void>() {
            @Override
            public void callback( final Void o ) {
                copyPopupView.hide();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
                view.hideBusyIndicator();
                refreshCommand.execute();
            }
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getCopyErrorCallback( final CopyPopupView copyPopupView ) {
        return new HasBusyIndicatorDefaultErrorCallback( view ) {

            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                copyPopupView.hide();
                return super.error( message, throwable );
            }
        };
    }

}
