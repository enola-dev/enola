/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.enola.common.io.resource.stream;

import static com.google.common.truth.Truth.assertThat;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileGlobResourceProviderTest {

    @ClassRule public static TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws IOException {
        var tempRoot = tempFolder.getRoot();
        Files.createFile(new File(tempRoot, "a.txt").toPath());
        Files.createFile(new File(tempRoot, "b.txt").toPath());
        Files.createFile(new File(tempRoot, "c.json").toPath());
        Files.createFile(new File(tempRoot, "d.yaml").toPath());

        // Note how we're intentionally being sneaky here and name the sub-directory
        // to match the *.txt glob used in the tests below. This is so that we test that
        // so named directories are be "searched into", but themselves ignored - because we're only
        // looking for files, but not returning the directories themselves.
        Files.createDirectory(new File(tempRoot, "subdir.txt/").toPath());
        Files.createFile(new File(tempRoot, "subdir.txt/e.txt").toPath());
    }

    private void check(String suffix, int expectedFiles) {
        var globIRI = tempFolder.getRoot().getAbsoluteFile().toURI().toString() + "/" + suffix;
        try (var stream = new FileGlobResourceProvider().get(globIRI)) {
            assertThat(stream).hasSize(expectedFiles);
        }
    }

    @Test
    public void globStarTXT() {
        check("*.txt", 2);
    }

    @Test
    public void globStarStarSlashStarTXT() {
        // **/*.txt only matches sub-dirs...
        check("**/*.txt", 1);
    }

    @Test
    public void globStarStarTXT() {
        // **.txt matches TXT in root and all sub-dirs...
        check("**.txt", 3);
    }

    @Test
    public void globStarCurly() {
        check("**.{txt,json,yaml}", 5);
    }

    @Test
    public void globQuestionMarkButWithoutAnyStar() {
        check("?.txt", 2);
    }

    @Test
    public void globSquareBracketButWithoutAnyStar() {
        check("[a-d].txt", 2);
    }

    @Test
    public void nonGlob() {
        check("a.txt", 1);
    }
}
