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

package org.kie.workbench.common.widgets.client.source;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorCallback;
import org.uberfire.ext.widgets.common.client.ace.AceEditorTheme;

public class DRLEditor
        extends Composite
        implements RequiresResize {

    private String input;

    interface ViewBinder
            extends
            UiBinder<Widget, DRLEditor> {

    }

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    public static int SCROLL_BAR_SIZE = 32;

    @UiField
    FlowPanel drlEditorContainer;

    private final AceEditor editor = new AceEditor();

    public DRLEditor() {
        initWidget(uiBinder.createAndBindUi(this));

        editor.startEditor();
        editor.setModeByName("drools");
        editor.setTheme(AceEditorTheme.CHROME);
        drlEditorContainer.add(editor);
    }

    public void setReadOnly( boolean readOnly ){
        editor.setReadOnly( true );
    }

    public void setText(final String input) {
        this.input = input;
        final String content;
        if (input == null) {
            content = "";
        } else {
            content = input;
        }
        editor.setText(content);
        editor.setFocus();
    }

    public void insertAtCursor(String ins) {
        editor.insertAtCursor(ins);
    }

    public String getText() {
        return editor.getValue();
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize(width,
                     height);
        editor.setHeight((height + SCROLL_BAR_SIZE) + "px");
        editor.redisplay();
    }

}

