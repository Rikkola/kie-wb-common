/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.explorer.client.widgets;

import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerOptions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectExplorerContextManagerTest {

    @Mock
    private ProjectExplorerContextItems projectExplorerContextItems;

    @Mock
    private BaseView baseView;

    @Mock
    private ProjectExplorerOptions projectExplorerOptions;

    @Spy
    @InjectMocks
    private ProjectExplorerContextManager projectExplorerContextManager;

    @Before
    public void setUp() {
        when( baseView.isVisible() ).thenReturn( true );
        doNothing().when( projectExplorerContextManager ).refresh();

        projectExplorerContextManager.init( baseView,
                                            projectExplorerOptions,
                                            null );
    }

    @Test
    public void removeActiveRepositoryTest() {
        GitRepository activeRepository = new GitRepository( "activeRepository" );
        when( projectExplorerContextItems.getActiveRepository() ).thenReturn( activeRepository );

        RepositoryRemovedEvent repositoryRemovedEvent = new RepositoryRemovedEvent( activeRepository );

        projectExplorerContextManager.onRepositoryRemovedEvent( repositoryRemovedEvent );

        verify( projectExplorerContextItems ).flush();
    }

    @Test
    public void removeInactiveRepositoryTest() {
        GitRepository activeRepository = new GitRepository( "activeRepository" );
        when( projectExplorerContextItems.getActiveRepository() ).thenReturn( activeRepository );

        GitRepository inactiveRepository = new GitRepository( "inactiveRepository" );
        RepositoryRemovedEvent repositoryRemovedEvent = new RepositoryRemovedEvent( inactiveRepository );

        projectExplorerContextManager.onRepositoryRemovedEvent( repositoryRemovedEvent );

        verify( projectExplorerContextItems, never() ).flush();
    }

    @Test
    public void removeInactiveRepositoryWithNoActiveRepositoryTest() {
        when( projectExplorerContextItems.getActiveRepository() ).thenReturn( null );

        GitRepository inactiveRepository = new GitRepository( "inactiveRepository" );
        RepositoryRemovedEvent repositoryRemovedEvent = new RepositoryRemovedEvent( inactiveRepository );

        projectExplorerContextManager.onRepositoryRemovedEvent( repositoryRemovedEvent );

        verify( projectExplorerContextItems, never() ).flush();
    }
}
