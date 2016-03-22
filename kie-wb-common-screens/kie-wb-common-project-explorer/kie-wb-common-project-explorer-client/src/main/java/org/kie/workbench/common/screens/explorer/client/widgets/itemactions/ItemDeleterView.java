package org.kie.workbench.common.screens.explorer.client.widgets.itemactions;

import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.ParameterizedCommand;

public interface ItemDeleterView
        extends HasBusyIndicator {

    void deleteItem( final ParameterizedCommand<String> command );

}
