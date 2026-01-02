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
package dev.enola.common;

import dev.dirs.ProjectDirectories;

import java.io.File;
import java.nio.file.Path;

/**
 * XDG Base Directories.
 *
 * <ul>
 *   <li><a
 *       href="https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html">Specification</a>
 *   <li><a href="https://wiki.archlinux.org/title/XDG_Base_Directory">Arch Linux Wiki</a>
 *   <li><a href="https://github.com/harawata/appdirs">harawata/appdirs</a>
 *   <li><a href="https://github.com/dirs-dev/directories-jvm">dirs-dev/directories-jvm</a>
 *   <li><a href="https://dirs.dev">dirs.dev</a>
 *   <li><a href="https://github.com/omajid/xdg-java">omajid/xdg-java</a>
 *   <li><a href="https://xdgbasedirectoryspecification.com/">xdgbasedirectoryspecification.com</a>
 * </ul>
 */
public final class FreedesktopDirectories {
    // TODO Rename FreedesktopDirectories to something like StandardPaths or so ?

    // TODO Do not hard-code app name, but leave configurable by "Product"

    // TODO Use https://github.com/harawata/appdirs instead of
    // https://codeberg.org/dirs/directories-jvm

    public static final File CACHE_FILE =
            new File(ProjectDirectories.from("dev", "Enola", "Enola").cacheDir);

    public static final Path HOSTKEY_PATH =
            Path.of(ProjectDirectories.from("dev", "Enola", "Enola").configDir, "sshd-hostkey");

    public static final Path PLAINTEXT_VAULT_FILE =
            Path.of(ProjectDirectories.from("dev", "Enola", "Enola").configDir, "azkaban.yaml");

    public static final Path HISTORY =
            Path.of(ProjectDirectories.from("dev", "Enola", "Enola").dataLocalDir, "history");

    public static final Path JLINE_CONFIG_DIR =
            Path.of(ProjectDirectories.from("dev", "Enola", "Enola").preferenceDir, "jline");

    private FreedesktopDirectories() {}
}
