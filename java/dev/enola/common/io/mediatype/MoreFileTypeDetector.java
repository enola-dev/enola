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
package dev.enola.common.io.mediatype;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.Map;

@AutoService(FileTypeDetector.class)
public class MoreFileTypeDetector extends FileTypeDetector {

    private static final Map<String, String> EXTENSION_TO_MIME_TYPE =
            ImmutableMap.of(
                    "markdown", MediaType.MD_UTF_8.withoutParameters().toString(),
                    "md", MediaType.MD_UTF_8.withoutParameters().toString(),
                    "yaml", YamlMediaType.YAML_UTF_8.withoutParameters().toString(),
                    "yml", YamlMediaType.YAML_UTF_8.withoutParameters().toString(),
                    "txt", MediaType.PLAIN_TEXT_UTF_8.withoutParameters().toString(),
                    "json", MediaType.JSON_UTF_8.withoutParameters().toString());

    @Override
    public @Nullable String probeContentType(Path path) {
        return detect(path);
    }

    static @Nullable String detect(Path path) {
        var fileName = path.getFileName();
        if (fileName == null) {
            return null;
        }
        String extension = Files.getFileExtension(fileName.toString());
        return EXTENSION_TO_MIME_TYPE.get(extension);
    }
}
