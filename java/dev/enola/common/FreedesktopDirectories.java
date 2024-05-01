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
package dev.enola.common;

import dev.dirs.ProjectDirectories;

import java.io.File;

/**
 * XDG Base Directories.
 *
 * <ul>
 *   <li><a
 *       href="https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html">Specification</a>
 *   <li><a href="https://wiki.archlinux.org/title/XDG_Base_Directory">Arch Linux Wiki</a>
 *   <li><a href="https://github.com/dirs-dev/directories-jvm">dirs-dev/directories-jvm</a>
 *   <li><a href="https://dirs.dev">dirs.dev</a>
 *   <li><a href="https://github.com/omajid/xdg-java">omajid/xdg-java</a>
 *   <li><a href="https://xdgbasedirectoryspecification.com/">xdgbasedirectoryspecification.com</a>
 * </ul>
 */
public final class FreedesktopDirectories {

    public static final File CACHE_FILE =
            new File(ProjectDirectories.from("dev", "Enola", "Enola").cacheDir);

    private FreedesktopDirectories() {}
}
