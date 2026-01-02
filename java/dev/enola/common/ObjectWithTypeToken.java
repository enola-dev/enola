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

import com.google.common.reflect.TypeToken;

public interface ObjectWithTypeToken<T> {

    T object();

    TypeToken<T> typeToken();

    @SuppressWarnings("unchecked")
    default <X> X as(TypeToken<?> typeToken) {
        if (!typeToken().equals(typeToken)) {
            throw new IllegalStateException(
                    "Requested " + typeToken.toString() + ", but I'm a " + typeToken().toString());
        }
        return (X) object();
    }
}
