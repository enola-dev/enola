/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

import static java.nio.file.Files.isDirectory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class FilePaths {

    public static Path requireDirectory(Path path) {
        if (!isDirectory(path)) throw new IllegalArgumentException("Not a directory: " + path);
        else return path;
    }

    public static boolean isIgnored(Path root, Path path) {
        Path rel = root.relativize(path);
        for (Path segment : rel) {
            if (segment.toString().startsWith(".")) {
                return true;
            }
        }
        return false;
    }

    public static void copyDirectory(Path input, Path output, Predicate<? super Path> filter) {
        try (Stream<Path> stream = Files.walk(requireDirectory(input))) {
            List<Path> files = stream.filter(Files::isRegularFile).filter(filter).toList();
            for (Path file : files) {
                Path rel = input.relativize(file);
                Path dest = output.resolve(rel);
                if (dest.getParent() != null) {
                    Files.createDirectories(dest.getParent());
                }
                Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void copyDirectory(Path input, Path output) {
        copyDirectory(input, output, path -> true);
    }

    private FilePaths() {}
}
