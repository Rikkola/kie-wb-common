package org.kie.workbench.common.screens.explorer.client.widgets.itemactions;

import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopupView;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface ItemRenamerView
        extends HasBusyIndicator {

    void renameItem( final Path path,
                     final Validator validator,
                     final CommandWithFileNameAndCommitMessage command,
                     final RenamePopupView renamePopupView );

}
