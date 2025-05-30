/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.function;

/**
 * {@link java.util.function.Supplier}-like functional interface which can throw a checked
 * exception.
 *
 * @param <T> the type of results supplied by this supplier
 * @param <E> the type of exception that may be thrown
 */
// https://javadocs.opendaylight.org/infrautils/neon/org/opendaylight/infrautils/utils/function/package-summary.html
@FunctionalInterface
public interface CheckedSupplier<T, E extends Exception> {
    T get() throws E;
}
