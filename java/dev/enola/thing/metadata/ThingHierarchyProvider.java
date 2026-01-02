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
package dev.enola.thing.metadata;

import com.google.common.collect.ImmutableList;

import dev.enola.thing.KIRI;
import dev.enola.thing.Link;
import dev.enola.thing.Thing;

import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Provider of _"hierarchical"_ (e.g. parent / child) relationships between Things.
 *
 * <p>This can be used e.g. to "cluster" things in visual graph diagram representations.
 */
public class ThingHierarchyProvider {

    private final String description;
    private final Iterable<String> propertyIRIs;

    public ThingHierarchyProvider() {
        // TODO Same as in ThingTimeProvider, properties eventually won't be hard-coded anymore
        this(
                "By Parent Hierarchy:",
                ImmutableList.of(
                        "https://enola.dev/parent",
                        "https://enola.dev/files/Node/parent", // TODO Node.parent_IRI,
                        KIRI.RDF.TYPE,
                        "http://www.w3.org/2000/01/rdf-schema#subClassOf",
                        "http://www.w3.org/2000/01/rdf-schema#subPropertyOf",
                        "https://enola.dev/origin"));
    }

    public ThingHierarchyProvider(String description, Iterable<String> propertyIRIs) {
        this.description = description;
        this.propertyIRIs = propertyIRIs;
    }

    public String description() {
        return description;
    }

    public Iterable<String> parents(Thing thing) {
        var list = new ArrayList<String>();
        for (var propertyIRI : propertyIRIs) {
            if (thing.isIterable(propertyIRI)) {
                var parents = thing.get(propertyIRI, Iterable.class);
                if (parents == null) continue;
                for (var parent : parents) {
                    // TODO Same story as in GraphvizGenerator
                    list.add(parent.toString());
                }
            } else {
                var parent = thing.getString(propertyIRI);
                if (parent != null) list.add(parent);
            }
        }
        return list;
    }

    public Optional<String> parent(Thing thing) {
        for (var propertyIRI : propertyIRIs) {
            if (thing.isIterable(propertyIRI)) {
                var parents = thing.get(propertyIRI, Iterable.class);
                if (parents == null) continue;
                var iterator = parents.iterator();
                if (!iterator.hasNext()) continue;
                var parent = iterator.next();
                return switch (parent) {
                    // TODO This is a kind of converter, and shouldn't be here?
                    case String string -> Optional.of(string);
                    case URI uri -> Optional.of(uri.toString());
                    case Link link -> Optional.of(link.iri());
                    default ->
                            throw new IllegalStateException(
                                    "Parent of unexpected type: "
                                            + parent.getClass()
                                            + " : "
                                            + parent);
                };
            } else {
                var parent = thing.getString(propertyIRI);
                if (parent != null) return Optional.of(parent);
            }
        }
        return Optional.empty();
    }
}
