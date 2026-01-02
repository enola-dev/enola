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
package dev.enola.common.linereader.jline;

import org.jline.reader.LineReader;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class InputRC {

    // TODO Upstream this into JLine org.jline.builtins.InputRC?
    //   https://github.com/jline/jline3/issues/1253

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(InputRC.class);

    public static void apply(LineReader lineReader) {
        var userHome = System.getProperty("user.home");
        if (userHome != null) {
            load(new File(userHome, ".inputrc"), lineReader);
        }

        load(new File("/etc/inputrc"), lineReader);
    }

    private static void load(File rcFile, LineReader lineReader) {
        if (rcFile.exists() && rcFile.isFile() && rcFile.canRead()) {
            try {
                try (var fileReader = new FileReader(rcFile, StandardCharsets.UTF_8)) {
                    org.jline.builtins.InputRC.configure(lineReader, fileReader);
                    LOG.info("Loaded {}", rcFile);
                }
            } catch (IOException e) {
                LOG.error("Failed to read inputrc file: {}", rcFile, e);
            }
        }
    }

    private InputRC() {}
}
