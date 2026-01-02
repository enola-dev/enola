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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/** FileGlobPathWalker is a utility to enumerate files matching a glob pattern. */
final class FileGlobPathWalker {

    /** See the {@link FileGlobResolver} documentation for glob path description. */
    static Stream<Path> walk(Path globPath) throws IOException {
        var globString = globPath.toString();
        var starPos = globString.indexOf('*');

        if (starPos == -1) starPos = globString.indexOf('?');

        if (starPos == -1) starPos = globString.indexOf('[');

        if (starPos > -1) {
            Path basePath;
            if (starPos > 0) basePath = Path.of(globString.substring(0, starPos - 1));
            else basePath = new File(".").toPath();

            // Inspired by File.newDirectoryStream(), but matching full path, not just getFileName()
            var fs = basePath.getFileSystem();
            var matcher = fs.getPathMatcher("glob:" + globString);
            var stream =
                    Files.walk(basePath, FileVisitOption.FOLLOW_LINKS)
                            .filter(path -> matcher.matches(path))
                            // .git/ contains weird files which have "standard" extensions but not
                            // content
                            .filter(path -> !path.toString().contains("/.git/"));
            // Add basePath, useful as "root dir", used e.g. in models/** DocGen FileThingConverter
            return Stream.concat(Stream.of(basePath), stream);
        } else {
            return Stream.of(globPath);
        }
    }

    private FileGlobPathWalker() {}
}
