/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import dev.enola.common.context.TLC;
import dev.enola.common.io.iri.URIs;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileGlobResolverTest {

    @ClassRule public static final TemporaryFolder tempFolder = new TemporaryFolder();

    protected GlobResolver newGlobResolver() {
        return new FileGlobResolver();
    }

    protected void checkGlobIRI(String globIRI, int expectedFiles) {
        try (var stream = newGlobResolver().get(globIRI)) {
            assertThat(stream).hasSize(expectedFiles);
        }
    }

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

    private void check(String suffix, int expectedURIs) {
        var globIRI = tempFolder.getRoot().getAbsoluteFile().toURI() + "/" + suffix;
        checkGlobIRI(globIRI, expectedURIs);
    }

    @Test
    public void globStarTXT() {
        check("*.txt", 4);
    }

    @Test
    public void globStarStarSlashStarTXT() {
        // **/*.txt only matches sub-dirs... (and root)
        check("**/*.txt", 2);
    }

    @Test
    public void globStarStarTXT() {
        // **.txt matches TXT in root and all sub-dirs...
        check("**.txt", 5);
    }

    @Test
    public void globStarStarTXTwithCharset() {
        // **.txt matches TXT in root and all sub-dirs, and root...
        check("**.txt?charset=US-ASCII", 5);
    }

    @Test
    public void globStarCurly() {
        check("**.{txt,json,yaml}", 7);
    }

    @Test
    public void globQuestionMarkButWithoutAnyStar() {
        check("?.txt", 3);
    }

    @Test
    public void globSquareBracketButWithoutAnyStar() {
        check("[a-d].txt", 3);
    }

    @Test
    public void nonGlob() {
        check("a.txt", 1);
    }

    @Test
    public void nonExistingNonGlob() {
        check("x.txt", 1);
    }

    @Test
    public void relativeGlobWithoutScheme() {
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            try (var stream = newGlobResolver().get("*.ttl")) {
                assertThat(stream).hasSize(1); // root directory
            }
        }
    }

    @Test // TODO Remove when support for (fake, wrong) file:*.ttl syntax is removed
    public void relativeGlobWithFileScheme() {
        try (var stream = newGlobResolver().get("file:*.ttl")) {
            assertThat(stream).hasSize(1); // root directory
        }
    }
}
