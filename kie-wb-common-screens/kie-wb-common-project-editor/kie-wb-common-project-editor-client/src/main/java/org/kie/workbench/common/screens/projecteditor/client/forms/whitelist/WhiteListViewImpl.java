/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.common.screens.projecteditor.client.forms.whitelist;

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

public class WhiteListViewImpl
        extends BaseModal
        implements WhiteListView {

    interface Binder
            extends
            UiBinder<Widget, WhiteListViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    ListBox availableDependencies;

    @UiField
    Button addButton;

    @UiField
    ListBox whiteList;

    public WhiteListViewImpl() {
        setTitle( ProjectEditorResources.CONSTANTS.WhiteListEditor() );
        setBody( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setAvailableDependencies( final Collection<String> data ) {
        for (String packageName : data) {
            availableDependencies.addItem( packageName );
        }
    }

    @Override
    public void setAvailablePackageNamesDisabled() {
        addButton.setEnabled( false );
        availableDependencies.setEnabled( false );
    }

    @Override
    public void showNoDependencies() {
        availableDependencies.addItem( ProjectEditorResources.CONSTANTS.NoDependencies() );
    }
}
