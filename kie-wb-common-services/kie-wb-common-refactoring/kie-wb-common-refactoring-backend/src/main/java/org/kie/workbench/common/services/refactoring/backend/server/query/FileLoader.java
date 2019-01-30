/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.refactoring.backend.server.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllLibraryAssetsQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryValueFileExtensionIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryValueRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.Files;

public class FileLoader {

    @Inject
    private RefactoringQueryService refactoringQueryService;

    public List<Path> loadPaths(final Path path,
                                final String suffix) {
        // Check Path exists
        final List<Path> items = new ArrayList<Path>();
        if (!Files.exists(Paths.convert(path))) {
            return items;
        }

        // Ensure Path represents a Folder
        org.uberfire.java.nio.file.Path pPath = Paths.convert(path);
        if (!Files.isDirectory(pPath)) {
            pPath = pPath.getParent();
        }

        final Set<ValueIndexTerm> queryTerms = new HashSet<>();

        final ArrayList<String> extensions = new ArrayList<>();
        extensions.add(suffix);
        queryTerms.add(new LibraryValueRepositoryRootIndexTerm(Paths.convert(pPath).toURI(), ValueIndexTerm.TermSearchType.NORMAL));
        queryTerms.add(new LibraryValueFileExtensionIndexTerm(extensions));

        final List<RefactoringPageRow> rows = refactoringQueryService.query(FindAllLibraryAssetsQuery.NAME,
                                                                            queryTerms);

        for (RefactoringPageRow row : rows) {
            items.add((Path) row.getValue());
        }

        return items;
    }
}
