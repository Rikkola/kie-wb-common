/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.commands.general;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.services.verifier.reporting.client.controller.AnalyzerController;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
public class DMNDecisionTableAnalyzerProvider {

    private final AnalysisReportScreen analysisReportScreen;
    private final PlaceManager placeManager;

    private DecisionTable model;

    @Inject
    public DMNDecisionTableAnalyzerProvider(final AnalysisReportScreen analysisReportScreen,
                                            final PlaceManager placeManager) {
        this.analysisReportScreen = analysisReportScreen;
        this.placeManager = placeManager;
    }

    public AnalyzerController newAnalyzer(Expression model) {
        if (model instanceof DecisionTable) {
            Window.alert("found dtable model ");
            this.model = (DecisionTable) model;
            return makePlaceHolder();
        } else {
            return makePlaceHolder();
        }
    }

    private AnalyzerController makePlaceHolder() {
        return new AnalyzerController() {
            @Override
            public void initialiseAnalysis() {

            }

            @Override
            public void terminateAnalysis() {

            }
        };
    }
}
