/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleDnDWorkbenchPanelPresenter;

@Templated
@WorkbenchPerspective(identifier = "TwitterBootstrapPerspective")
public class TwitterBootstrapPerspective implements IsElement {

    @Inject
    @DataField
    @WorkbenchPanel(panelType = MultiTabWorkbenchPanelPresenter.class,
            parts = "MoodScreen")
    Div tabPanel;

    @Inject
    @DataField
    @WorkbenchPanel(panelType = MultiListWorkbenchPanelPresenter.class,
            parts = "HelloWorldScreen")
    Div listPanel;

    @Inject
    @DataField
    @WorkbenchPanel(panelType = SimpleDnDWorkbenchPanelPresenter.class,
            parts = "HomeScreen")
    Div simplePanel;
}