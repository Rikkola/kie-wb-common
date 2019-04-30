/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.verifier.reporting.client.panel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

public class GearMenuItemBuilder implements MenuFactory.CustomMenuBuilder {

    private List<AnchorListItem> items = new ArrayList<>();

    public GearMenuItemBuilder(final GearMenuItem... gearMenuItems) {
        for (GearMenuItem item : gearMenuItems) {
            final AnchorListItem anchorListItem = new AnchorListItem(item.text);
            anchorListItem.addClickHandler(event -> item.command.execute());
            items.add(anchorListItem);
        }
    }

    @Override
    public void push(MenuFactory.CustomMenuBuilder element) {
        //Do nothing
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public Widget build() {
                return new ButtonGroup() {{
                    add(new Button() {{
                        setToggleCaret(false);
                        setDataToggle(Toggle.DROPDOWN);
                        setIcon(IconType.COG);
                        setSize(ButtonSize.SMALL);
                        setTitle("ProjectExplorerConstants.INSTANCE.customizeView()");
                    }});
                    add(new DropDownMenu() {{
                        addStyleName("pull-right");
                        items.forEach(item -> add(item));
                    }});
                }};
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };
    }

    public static class GearMenuItem {

        private final String text;
        private final Command command;

        public GearMenuItem(final String text,
                            final Command command) {

            this.text = text;
            this.command = command;
        }
    }
}
