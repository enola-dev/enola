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
package dev.enola.common.exec;

import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ExecPATH {

    // TODO Upstream; see https://github.com/vorburger/ch.vorburger.exec/issues/277

    // TODO Windows support is just quick hack, and has not really been tested yet.

    // TODO Add missing test coverage; use e.g. JimFS to mock a file system.

    private static final boolean IS_WINDOWS =
            System.getProperty("os.name").toLowerCase().contains("win");

    private static final String[] WINDOWS_EXECUTABLE_EXTENSIONS = {".exe", ".bat", ".cmd"};

    public static ImmutableMap<String, File> scan() {
        List<File> executables = findExecutablesOnPATH();
        ImmutableMap.Builder<String, File> builder =
                ImmutableMap.builderWithExpectedSize(executables.size());
        // Reverse list to keep the first executable found on PATH with builder.buildKeepingLast()
        for (File executable : executables.reversed()) {
            builder.put(chop(executable.getName()), executable);
        }
        return builder.buildKeepingLast();
    }

    private static String chop(String fileName) {
        if (IS_WINDOWS) {
            for (String extension : WINDOWS_EXECUTABLE_EXTENSIONS) {
                if (fileName.toLowerCase().endsWith(extension)) {
                    return fileName.substring(0, fileName.length() - extension.length());
                }
            }
        }
        return fileName;
    }

    private static List<File> findExecutablesOnPATH() {
        List<File> executables = new ArrayList<>();
        String path = System.getenv("PATH");
        if (path != null) {
            String[] pathDirs = path.split(File.pathSeparator);
            for (String dirPath : pathDirs) {
                File dir = new File(dirPath);
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            if (file.canExecute()) {
                                executables.add(file.getAbsoluteFile());
                            } else if (IS_WINDOWS) {
                                for (String ext : WINDOWS_EXECUTABLE_EXTENSIONS) {
                                    if (file.getName().toLowerCase().endsWith(ext)) {
                                        executables.add(file.getAbsoluteFile());
                                        break; // Found a match, no need to check other extensions
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return executables;
    }

    private ExecPATH() {}
}
