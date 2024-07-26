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
package dev.enola.model.enola.meta.bootstrap;

import dev.enola.model.enola.meta.Schema;
import dev.enola.model.enola.meta.Type;

// NB: This hand-written class may eventually get replaced by a code-generated one!
public abstract class MutableType extends MutableCommon implements Type, Type.Builder {

    // TODO Use String ID instead of Schema, and use TLC/TP get() in accessors?!
    private Schema schema;

    @Override
    public Schema schema() {
        return schema;
    }

    @Override
    public Type.Builder schema(Schema schema) {
        this.schema = schema;
        return this;
    }
}
