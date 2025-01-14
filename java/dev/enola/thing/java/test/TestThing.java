/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.java.test;

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.java.IRI;
import dev.enola.thing.java.JThing;

import org.jspecify.annotations.Nullable;

@JThing("https://enola.dev/TestThing")
public interface TestThing extends Thing {

    // TODO Replace with code gen. from test.esch.yaml

    // NB: This is only here like this for TestThingTest; otherwise this would be inlined in @IRI!
    String NUMBER_URI = "https://enola.dev/test/number";

    @IRI(NUMBER_URI)
    @Nullable Integer number();

    @IRI(KIRI.RDFS.LABEL)
    @Nullable String label();

    // TODO Generate this, with APT? (Needs moving into separate top-level class.) Or gen. all?
    interface Builder<B extends TestThing> extends Thing.Builder<B> { // skipcq: JAVA-E0169
        Builder<B> label(String label);

        Builder<B> number(Integer number);
    }
}
