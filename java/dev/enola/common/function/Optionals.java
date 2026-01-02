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
package dev.enola.common.function;

import java.util.Optional;

public class Optionals {

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalOfNullableMisuse"})
    public static <T, U, E extends Exception> Optional<U> map(
            Optional<T> optional, CheckedFunction<? super T, ? extends U, E> mapper) throws E {
        if (optional.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(mapper.apply(optional.get()));
        }
    }
}
