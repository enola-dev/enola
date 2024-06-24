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
package dev.enola.thing.repo;

import static java.util.Objects.requireNonNull;

public class ThingProviderThreadLocal implements AutoCloseable {

    private static final ThreadLocal<ThingProvider> threadLocal = new ThreadLocal<>();

    public ThingProviderThreadLocal(ThingProvider provider) {
        threadLocal.set(requireNonNull(provider, "ThingProvider must not be null"));
    }

    public static ThingProvider get() {
        ThingProvider thingProvider = threadLocal.get();
        if (thingProvider == null) {
            throw new IllegalStateException("No ThingProvider; already closed?");
        }
        return thingProvider;
    }

    @Override
    public void close() throws Exception {
        threadLocal.remove();
    }
}
