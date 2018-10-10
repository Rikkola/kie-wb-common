/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.security.shared.api.identity.User;

@ApplicationScoped
public class TestRuntimeReportingService {

    protected User identity;
    private Event<OnShowTestPanelEvent> showTestPanelEvent;

    private ListDataProvider<Failure> dataProvider = new ListDataProvider<Failure>();

    public TestRuntimeReportingService() {
    }

    @Inject
    public TestRuntimeReportingService(
            User identity,
            Event<OnShowTestPanelEvent> showTestPanelEvent) {
        this.identity = identity;
        this.showTestPanelEvent = showTestPanelEvent;
    }

    public void addBuildMessages(final @Observes TestResultMessage message) {

        if (message.getIdentifier().equals(identity.getIdentifier())) {
            dataProvider.getList().clear();

            if (!message.wasSuccessful()) {
                dataProvider.getList().addAll(message.getFailures());
                dataProvider.flush();
            }
            showTestPanelEvent.fire(new OnShowTestPanelEvent());
        }
    }

    public void addDataDisplay(HasData<Failure> failures) {
        dataProvider.addDataDisplay(failures);
    }
}
