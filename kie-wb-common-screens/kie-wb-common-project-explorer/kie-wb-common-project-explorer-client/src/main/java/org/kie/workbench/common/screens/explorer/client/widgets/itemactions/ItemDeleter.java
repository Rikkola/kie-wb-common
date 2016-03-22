package org.kie.workbench.common.screens.explorer.client.widgets.itemactions;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class ItemDeleter {

    private Caller<ExplorerService>  explorerService;
    private ItemDeleterView          view;
    private Event<NotificationEvent> notification;

    @Inject
    public ItemDeleter( final ItemDeleterView view,
                        final Caller<ExplorerService> explorerService,
                        final Event<NotificationEvent> notification ) {
        this.view = view;
        this.explorerService = explorerService;
        this.notification = notification;
    }

    public void delete( final FolderItem folderItem,
                        final Command refreshCommand ) {

        view.deleteItem( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String comment ) {
                view.showBusyIndicator( CommonConstants.INSTANCE.Deleting() );

                explorerService.call( new RemoteCallback<Object>() {
                                          @Override
                                          public void callback( Object o ) {
                                              notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
                                              refreshCommand.execute();
                                          }
                                      },
                                      new HasBusyIndicatorDefaultErrorCallback( view ) ).deleteItem( folderItem,
                                                                                                     comment );
            }
        } );
    }
}
