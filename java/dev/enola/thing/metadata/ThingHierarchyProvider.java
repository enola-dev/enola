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
package dev.enola.thing.metadata;

import dev.enola.thing.Thing;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Provider of _"hierarchical"_ (e.g. parent / child) relationships between Things.
 *
 * <p>This can be used e.g. to "cluster" things in visual graph diagram representations.
 */
public class ThingHierarchyProvider {

    public Iterable<String> parents(Thing iri) {
        // TODO Same as in ThingTimeProvider, this eventually won't be hard-coded anymore
        var parentIRI = "https://enola.dev/parent";
        var fileParent = "https://enola.dev/files/Node/parent/";
        var rdfType = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
        var rdfsSubClassOf = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
        var rdfsSubPropertyOf = "http://www.w3.org/2000/01/rdf-schema#subPropertyOf";

        var list = new ArrayList<String>();
        add(list, iri.getString(parentIRI));
        add(list, iri.getString(fileParent));
        add(list, iri.getString(rdfType));
        add(list, iri.getString(rdfsSubClassOf));
        add(list, iri.getString(rdfsSubPropertyOf));
        return list;
    }

    private void add(List<String> list, @Nullable String string) {
        if (string != null) list.add(string);
    }
}
