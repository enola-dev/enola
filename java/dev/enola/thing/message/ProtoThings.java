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
package dev.enola.thing.message;

import com.google.common.collect.Iterables;

import dev.enola.common.context.TLC;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.proto.Thing;

import java.util.ArrayList;

public final class ProtoThings {
    private ProtoThings() {}

    public static Iterable<dev.enola.thing.Thing> proto2java(Iterable<Thing> protoThings) {
        var dtr = TLC.get(DatatypeRepository.class);
        var javaThings = new ArrayList<dev.enola.thing.Thing>(Iterables.size(protoThings));
        for (var protoThing : protoThings) {
            javaThings.add(new ThingAdapter(protoThing, dtr));
        }
        return javaThings;
    }
}
