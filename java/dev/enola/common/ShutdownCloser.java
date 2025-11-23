/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import static java.util.Collections.synchronizedSet;

import java.util.HashSet;
import java.util.Set;

/**
 * A utility class for gracefully closing resources (AutoCloseable instances) when the Java Virtual
 * Machine (JVM) shuts down. Typically used in main() methods to ensure that all resources are
 * closed when the application is terminated with Ctrl+C or a kill signal (which will not run
 * try-with-resources).
 */
public final class ShutdownCloser {
    private ShutdownCloser() {}

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ShutdownCloser::closeAll));
    }

    private static final Set<AutoCloseable> RESOURCES = synchronizedSet(new HashSet<>());

    /**
     * Adds an AutoCloseable resource to the static set for graceful shutdown.
     *
     * @param resource The resource to be closed on JVM shutdown.
     */
    public static void add(AutoCloseable resource) {
        if (resource != null) {
            RESOURCES.add(resource);
        }
    }

    private static void closeAll() {
        Set<AutoCloseable> resourcesToClose;
        synchronized (RESOURCES) {
            resourcesToClose = new HashSet<>(RESOURCES);
            RESOURCES.clear();
        }

        for (AutoCloseable resource : resourcesToClose) {
            try {
                resource.close();

            } catch (Throwable t) {
                // Important: Catch exceptions to ensure remaining resources can still be closed
                System.err.printf(
                        "[Shutdown Hook] Error closing resource %s: %s\n",
                        resource.getClass().getSimpleName(), t.getMessage());
                t.printStackTrace(System.err);
            }
        }
    }
}
