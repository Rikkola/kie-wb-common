package org.kie.workbench.common.screens.explorer.client.widgets.itemactions;

import org.uberfire.ext.editor.commons.client.file.DeletePopup;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.mvp.ParameterizedCommand;

public class ItemDeleterViewImpl
        implements ItemDeleterView {

    @Override
    public void deleteItem( final ParameterizedCommand<String> command ) {
        final DeletePopup popup = new DeletePopup( command );
        popup.show();
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
