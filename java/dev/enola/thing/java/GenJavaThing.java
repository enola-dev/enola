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
package dev.enola.thing.java;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.datatype.Datatype;
import dev.enola.thing.impl.ImmutableThing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Immutable
@ThreadSafe
@SuppressFBWarnings(
        value = "EQ_DOESNT_OVERRIDE_EQUALS",
        justification =
                "equals() and hashCode() are intentionally not overriden, because it works as-is")
// skipcq: JAVA-W0100
public abstract class GenJavaThing extends ImmutableThing {

    // TODO Try to remove this field; it technically duplicates something we inherit from
    // ImmutablePredicatesObjects#properties already; so this field ideally shouldn't be needed...
    private final ImmutableSet<String> predicateIRIs;

    protected GenJavaThing(
            String iri,
            ImmutableMap<String, Datatype<?>> fields_datatypes,
            ImmutableMap<String, Object> dynamic_properties,
            ImmutableMap<String, String> dynamic_datatypes) {
        super(
                iri,
                dynamic_properties,
                ImmutableMap.<String, String>builder()
                        .putAll(map(fields_datatypes))
                        .putAll(dynamic_datatypes)
                        .build());
        this.predicateIRIs =
                ImmutableSet.<String>builder()
                        .addAll(fields_datatypes.keySet())
                        .addAll(dynamic_properties.keySet())
                        // No Need: .addAll(dynamic_datatypes.keySet())
                        .build();
    }

    private static ImmutableMap<String, String> map(ImmutableMap<String, Datatype<?>> datatypes) {
        var map = ImmutableMap.<String, String>builderWithExpectedSize(datatypes.size());
        datatypes.forEach((predicateIRI, datatype) -> map.put(predicateIRI, datatype.iri()));
        return map.build();
    }

    @Override
    public ImmutableSet<String> predicateIRIs() {
        return predicateIRIs;
    }
}
