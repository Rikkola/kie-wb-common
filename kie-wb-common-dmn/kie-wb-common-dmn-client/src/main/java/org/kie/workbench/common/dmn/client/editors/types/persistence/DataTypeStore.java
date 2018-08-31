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

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.client.editors.types.DataType;

@ApplicationScoped
public class DataTypeStore {

    private Map<String, DataType> dataTypes = new HashMap<>();

    public DataType get(final String id) {
        return dataTypes.get(id);
    }

    public void index(final String uuid,
                      final DataType dataType) {
        dataTypes.put(uuid, dataType);
    }

    public void clear() {
        dataTypes.clear();
    }

    int size() {
        return dataTypes.size();
    }
}
