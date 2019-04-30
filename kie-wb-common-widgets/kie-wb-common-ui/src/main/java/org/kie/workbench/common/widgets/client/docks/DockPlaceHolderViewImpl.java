package org.kie.workbench.common.widgets.client.docks;

import com.google.gwt.user.client.ui.SimplePanel;

public class DockPlaceHolderViewImpl
        extends SimplePanel
        implements DockPlaceHolderView {

    private DockPlaceHolder presenter;

    @Override
    public DockPlaceHolder getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(DockPlaceHolder presenter) {
        this.presenter = presenter;
    }
}
