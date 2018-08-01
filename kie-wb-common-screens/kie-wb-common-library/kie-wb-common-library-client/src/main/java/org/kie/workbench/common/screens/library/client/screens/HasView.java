package org.kie.workbench.common.screens.library.client.screens;

import org.uberfire.client.mvp.UberElement;

/**
 * Created by tonirikkola on 31.7.2018.
 */
public interface HasView<T> {

    UberElement<T> getView();
}
