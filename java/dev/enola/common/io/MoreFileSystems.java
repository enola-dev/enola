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
package dev.enola.common.io;

import com.google.common.collect.ImmutableSet;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.spi.FileSystemProvider;

/** Utilities for {@link FileSystem}, similar to {@link FileSystems}. */
public final class MoreFileSystems {
    private MoreFileSystems() {}

    /** Set of registered FileSystem schemas; e.g. "file" (for "file:///...") and "jar" etc. */
    public static final ImmutableSet<String> URI_SCHEMAS = getFileSystemSchemes();

    private static ImmutableSet<String> getFileSystemSchemes() {
        var builder = ImmutableSet.<String>builder();
        for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
            // TODO Add full support for the jar: FileSystem? It's intentionally not supported,
            // yet... because it would need more work; see
            // https://stackoverflow.com/q/25032716/421602
            // https://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html
            // https://docs.oracle.com/en/java/javase/21/docs/api/jdk.zipfs/module-summary.html
            if (!provider.getScheme().equals("jar")) {
                builder.add(provider.getScheme());
            }
        }
        return builder.build();
    }
}
