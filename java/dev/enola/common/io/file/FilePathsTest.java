/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.file;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.io.file.FilePaths.copyDirectory;
import static dev.enola.common.io.file.FilePaths.isIgnored;
import static dev.enola.common.io.file.FilePaths.requireDirectory;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FilePathsTest {

    @Test
    void requireDirectoryValid(@TempDir Path tempDir) {
        assertThat(requireDirectory(tempDir)).isEqualTo(tempDir);
    }

    @Test
    void requireDirectoryInvalid(@TempDir Path tempDir) throws IOException {
        Path file = Files.writeString(tempDir.resolve("file.txt"), "hello");
        assertThrows(IllegalArgumentException.class, () -> requireDirectory(file));
        assertThrows(
                IllegalArgumentException.class,
                () -> requireDirectory(tempDir.resolve("non-existent")));
    }

    @Test
    void isIgnoredCheck() {
        Path root = Path.of("/a/b");
        assertThat(isIgnored(root, Path.of("/a/b/.hidden/file.txt"))).isTrue();
        assertThat(isIgnored(root, Path.of("/a/b/sub/.hidden"))).isTrue();
        assertThat(isIgnored(root, Path.of("/a/b/sub/file.txt"))).isFalse();
    }

    @Test
    void copyDirectoryAll(@TempDir Path tempDir) throws IOException {
        Path input = Files.createDirectory(tempDir.resolve("input"));
        Path sub = Files.createDirectories(input.resolve("sub/nested"));
        Files.writeString(input.resolve("root.txt"), "root");
        Files.writeString(sub.resolve("nested.txt"), "nested");

        Path output = tempDir.resolve("output");
        copyDirectory(input, output);

        assertThat(Files.readString(output.resolve("root.txt"))).isEqualTo("root");
        assertThat(Files.readString(output.resolve("sub/nested/nested.txt"))).isEqualTo("nested");
    }

    @Test
    void copyDirectoryWithFilter(@TempDir Path tempDir) throws IOException {
        Path input = Files.createDirectory(tempDir.resolve("input"));
        Path sub = Files.createDirectories(input.resolve("sub"));
        Files.writeString(input.resolve("keep.txt"), "keep");
        Files.writeString(input.resolve("ignore.md"), "ignore");
        Files.writeString(sub.resolve("sub-keep.txt"), "sub-keep");
        Files.writeString(sub.resolve("sub-ignore.md"), "sub-ignore");

        Path output = tempDir.resolve("output");
        copyDirectory(input, output, path -> path.getFileName().toString().endsWith(".txt"));

        assertThat(Files.exists(output.resolve("keep.txt"))).isTrue();
        assertThat(Files.exists(output.resolve("sub/sub-keep.txt"))).isTrue();
        assertThat(Files.exists(output.resolve("ignore.md"))).isFalse();
        assertThat(Files.exists(output.resolve("sub/sub-ignore.md"))).isFalse();
    }
}
