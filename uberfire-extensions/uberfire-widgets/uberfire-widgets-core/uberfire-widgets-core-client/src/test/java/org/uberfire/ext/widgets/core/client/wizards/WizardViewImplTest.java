/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.core.client.wizards;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class WizardViewImplTest {

    @Mock
    WizardPopupFooter footer;

    @GwtMock
    WizardViewImpl view;

    @Before
    public void init() {
        view.footer = footer;
        doCallRealMethod().when(view).setCompletionStatus(anyBoolean());
    }

    @Test
    public void testSetCompletionStatusTrue() {
        view.setCompletionStatus(true);
        verify(view.footer,
               times(1)).enableFinishButton(true);
    }

    @Test
    public void testSetCompletionStatusFalse() {
        view.setCompletionStatus(false);
        verify(view.footer,
               times(1)).enableFinishButton(false);
    }
}
