package org.kie.workbench.common.screens.explorer.client.widgets.itemactions;

import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopup;
import org.uberfire.ext.editor.commons.client.file.RenamePopupView;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

public class ItemRenamerViewImpl
        implements ItemRenamerView {

    @Override
    public void renameItem( final Path path,
                            final Validator validator,
                            final CommandWithFileNameAndCommitMessage command,
                            final RenamePopupView renamePopupView ) {
        final RenamePopup popup = new RenamePopup( path,
                                                   validator,
                                                   command,
                                                   renamePopupView );
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
