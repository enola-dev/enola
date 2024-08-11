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

import dev.enola.common.time.Interval;
import dev.enola.thing.repo.ThingProvider;

import java.time.Instant;
import java.util.List;

/**
 * Provider of _"temporal"_ <a href="https://en.wikipedia.org/wiki/Time">Time</a> related metadata
 * about Things.
 */
public class ThingTimeProvider {

    private final ThingProvider tp;

    public ThingTimeProvider(ThingProvider tp) {
        this.tp = tp;
    }

    /**
     * Provides the (non-overlapping) {@link Interval}s (plural!) during which the Thing at this IRI
     * "exists".
     */
    public Iterable<Interval> existance(String iri) {
        var thing = tp.get(iri);
        if (thing == null) return List.of();

        // TODO The following is intentionally hard-coded initially, but the thinking is that
        // eventually the IRIs of these properties should be inferred e.g. using Enola Meta schema
        // rdfs:subPropertyOf ...

        var createdAtIRI = "https://enola.dev/createdAt";
        var startedAtIRI = "https://enola.dev/startedAt";
        var fileCreatedAt = "https://enola.dev/files/Node/createdAt";
        var start =
                thing.getOptional(createdAtIRI, Instant.class)
                        .or(() -> thing.getOptional(startedAtIRI, Instant.class))
                        .or(() -> thing.getOptional(fileCreatedAt, Instant.class))
                        .orElse(Instant.MIN);

        var deleteAtIRI = "https://enola.dev/deletedAt";
        var endedAtIRI = "https://enola.dev/endedAt";
        var fileDeletedAt = "https://enola.dev/files/Node/deletedAt";
        var end =
                thing.getOptional(deleteAtIRI, Instant.class)
                        .or(() -> thing.getOptional(endedAtIRI, Instant.class))
                        .or(() -> thing.getOptional(fileDeletedAt, Instant.class))
                        .orElse(Instant.MAX);

        return List.of(Interval.of(start, end));
    }
}
