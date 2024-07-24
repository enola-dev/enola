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

import dev.enola.common.context.TLC;
import dev.enola.model.enola.meta.Class;
import dev.enola.model.enola.meta.Datatype;
import dev.enola.thing.repo.ThingProvider;

public class MutableDatatype extends MutableType implements Datatype, Datatype.Builder {

    private Class java;
    private Datatype datatype;

    @Override
    public Class type() {
        return (Class) TLC.get(ThingProvider.class).get("https://enola.dev/meta/Datatype");
    }

    @Override
    public Class java() {
        return java;
    }

    @Override
    public Datatype java(Class java) {
        this.java = java;
        return this;
    }

    @Override
    public Datatype parent() {
        return null;
    }

    @Override
    public Datatype parent(Datatype datatype) {
        this.datatype = datatype;
        return this;
    }

    @Override
    public Datatype build() {
        return this;
    }
}
