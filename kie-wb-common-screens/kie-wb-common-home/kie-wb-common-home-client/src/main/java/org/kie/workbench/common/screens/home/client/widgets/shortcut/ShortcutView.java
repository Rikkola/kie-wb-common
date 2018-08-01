/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.home.client.widgets.shortcut;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.subheading.ShortcutSubHeadingLinkPresenter;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.subheading.ShortcutSubHeadingTextPresenter;
import org.uberfire.mvp.Command;

@Templated
public class ShortcutView implements ShortcutPresenter.View,
                                     IsElement {

    private ShortcutPresenter presenter;

    @Inject
    @DataField("card")
    Div card;

    @Inject
    @DataField("icon")
    Div icon;

    @Inject
    @Named("h2")
    @DataField("heading")
    Heading heading;

    @Inject
    @DataField("sub-heading")
    Div subHeading;

    @Override
    public void init(final ShortcutPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addIconClass(final String iconClass) {
        icon.getClassList().add(iconClass);
    }

    @Override
    public void setHeading(final String heading) {
        this.heading.setTextContent(heading);
        this.card.setId("home-action-" + heading.toLowerCase());
    }

    @Override
    public void setAction(final Command action) {
        card.setOnclick(event -> action.execute());
    }

    @Override
    public void addSubHeadingChild(ShortcutSubHeadingLinkPresenter linkPresenter) {
        subHeading.appendChild(linkPresenter.getView().getElement());
    }

    @Override
    public void addSubHeadingChild(ShortcutSubHeadingTextPresenter text) {
        subHeading.appendChild(text.getView().getElement());
    }
}
