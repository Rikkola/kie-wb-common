package org.kie.workbench.common.screens.explorer.client.widgets.itemactions;

import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.CopyPopup;
import org.uberfire.ext.editor.commons.client.file.CopyPopupView;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

public class ItemCopierViewImpl
        implements ItemCopierView {

    @Override
    public void copyItem( final Path path,
                          final Validator validator,
                          final CommandWithFileNameAndCommitMessage command,
                          final CopyPopupView copyPopupView ) {
        final CopyPopup popup = new CopyPopup( path,
                                               validator,
                                               command,
                                               copyPopupView );
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
