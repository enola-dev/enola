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
package dev.enola.model.enola.meta;

import dev.enola.thing.KIRI;

public interface Type extends Common {

    default Schema schema() {
        return getThingOrThrow(KIRI.E.META.SCHEMA, Schema.class);
    }

    interface Builder<B extends Type> // skipcq: JAVA-E0169
            extends Type, Common.Builder<B> {

        default Type.Builder<B> schema(Schema schema) {
            set(KIRI.E.META.SCHEMA, schema);
            return this;
        }
    }
}
