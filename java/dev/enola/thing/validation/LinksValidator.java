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
package dev.enola.thing.validation;

import dev.enola.thing.Link;
import dev.enola.thing.PredicatesObjects;
import dev.enola.thing.repo.ThingRepository;

import java.net.URI;

public class LinksValidator implements Validator<PredicatesObjects> {

    private final ThingRepository repo;

    public LinksValidator(ThingRepository repo) {
        this.repo = repo;
    }

    @Override
    public void validate(PredicatesObjects thing, Collector collector) {
        thing.datatypes()
                .forEach((predicateIRI, datatypeIRI) -> c(predicateIRI, datatypeIRI, collector));
        thing.properties().forEach((predicateIRI, object) -> c(predicateIRI, object, collector));
    }

    private void c(String predicateIRI, String linkedIRI, Collector collector) {
        if (linkedIRI.startsWith("file:"))
            // TODO Remove this workaround again eventually
            // file: shouldn't be Links in the first place (but :URL)
            return;

        if (repo.get(linkedIRI) == null) {
            collector.add(predicateIRI, "Unknown thing: " + linkedIRI);
        }
    }

    private void c(String predicateIRI, Object object, Collector collector) {
        if (object instanceof Link link) {
            c(predicateIRI, link.iri(), collector);
        } else if (object instanceof URI uri) {
            c(predicateIRI, uri.toString(), collector);
        } else if (object instanceof PredicatesObjects predicatesObjects) {
            validate(predicatesObjects, collector);
        } else if (object instanceof Iterable<?> iterable) {
            for (var element : iterable) {
                if (element instanceof PredicatesObjects thing) {
                    validate(thing, collector);
                }
            }
        } // else skip it
    }
}
