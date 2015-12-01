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
package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import java.util.Collection;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

public class WhiteListPresenter {

    private WhiteListView view;
    private Caller<DependencyService> dependencyService;

    public WhiteListPresenter() {

    }

    @Inject
    public WhiteListPresenter( final WhiteListView view,
                               final Caller<DependencyService> dependencyService ) {
        this();
        this.view = view;
        this.dependencyService = dependencyService;
    }

    public void show( final GAV gav ) {
        dependencyService.call(
                new RemoteCallback<Collection<Dependency>>() {
                    @Override
                    public void callback( Collection<Dependency> allDependencies ) {
                        view.setData( allDependencies.toString() );
                        view.show();
                    }
                }, new DefaultErrorCallback() ).loadDependencies( gav );
    }
}
