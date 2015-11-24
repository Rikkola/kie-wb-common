/*
 * Copyright 2013 JBoss Inc
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

import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.forms.GAVSelectionHandler;
import org.kie.workbench.common.screens.projecteditor.service.DependencyService;

@Dependent
public class DependencyGrid
        implements IsWidget {

    private DependencyGridView view;
    private DependencySelectorPopup dependencySelectorPopup;

    private Caller<DependencyService> dependencyService;

    private Dependencies allDependencies = new Dependencies();
    private POM pom;

    public DependencyGrid() {
    }

    @Inject
    public DependencyGrid( final DependencySelectorPopup dependencySelectorPopup,
                           final DependencyGridView view,
                           final Caller<DependencyService> dependencyService ) {
        this.dependencySelectorPopup = dependencySelectorPopup;
        this.dependencyService = dependencyService;

        dependencySelectorPopup.addSelectionHandler( new GAVSelectionHandler() {
            @Override
            public void onSelection( GAV gav ) {
                pom.getDependencies().add( new Dependency( gav ) );
                show();
            }
        } );

        this.view = view;
        view.setPresenter( this );
    }

    public void setDependencies( POM pom ) {
        this.pom = pom;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void onAddDependencyButton() {
        pom.getDependencies().add( new Dependency() );
        show();
    }

    public void onAddDependencyFromRepositoryButton() {
        dependencySelectorPopup.show();
    }

    public void onRemoveDependency( final Dependency dependency ) {
        pom.getDependencies().remove( dependency );
        show();
    }

    public void setReadOnly() {
        view.setReadOnly();
    }

    public void show() {
        dependencyService.call(
                new RemoteCallback<Collection<Dependency>>() {
                    @Override
                    public void callback( Collection<Dependency> allDependencies ) {

                        DependencyGrid.this.allDependencies.clear();
                        DependencyGrid.this.allDependencies.addAll( allDependencies );

                        ArrayList<Dependency> result = new ArrayList<Dependency>( allDependencies );

                        for (Dependency dependency : allDependencies) {
                            if ( !pom.getDependencies().containsDependency( dependency ) ) {
                                dependency.setScope( "transient" );
                            }
                        }

                        Dependencies allDeps = new Dependencies( new ArrayList<Dependency>( allDependencies ) );
                        for (Dependency dependency : pom.getDependencies()) {
                            if ( !allDeps.containsDependency( dependency ) ) {
                                result.add( dependency );
                            }
                        }

                        view.show( result );
                    }
                }, new ErrorCallback() {
                    @Override
                    public boolean error( Object o, Throwable throwable ) {
//                        view.showDependenciesPanel( Collections.EMPTY_LIST );
                        return false;
                    }
                } ).loadDependencies( pom );
    }
}
