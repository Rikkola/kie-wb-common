/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.social.hp.client.homepage.main;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.ext.uberfire.social.activities.client.widgets.timeline.regular.SocialTimelineWidget;

@Dependent
public class MainPresenter {

    public View getView() {
        return view;
    }

    @Inject
    private View view;

    public interface View extends IsWidget {

        void setSocialWidget( SocialTimelineWidget socialTimelineWidget );
    }

    public void setSocialWidget( SocialTimelineWidget socialTimelineWidget ) {
        view.setSocialWidget(socialTimelineWidget);
    }

    @PostConstruct
    public void setup() {
    }


}