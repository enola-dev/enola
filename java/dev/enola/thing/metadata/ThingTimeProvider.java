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

import dev.enola.common.time.Interval;
import dev.enola.thing.Thing;

import java.time.Instant;
import java.util.List;

/**
 * Provider of _"temporal"_ <a href="https://en.wikipedia.org/wiki/Time">Time</a> related metadata
 * about Things.
 */
public class ThingTimeProvider {

    /**
     * Provides the (non-overlapping) {@link Interval}s (plural!) during which the Thing at this IRI
     * "exists".
     *
     * <p>This always returns at least 1 Interval, never none (or null) - but it may well be the
     * {@link Interval#ALL} if the Thing has no temporal metadata.
     */
    public Iterable<Interval> existance(Thing thing) {

        // TODO The following is intentionally hard-coded initially, but the thinking is that
        // eventually the IRIs of these properties should be inferred e.g. using Enola Meta schema
        // rdfs:subPropertyOf ...

        // Note that the orders, for both start & end, are intentionally by priority we're
        // considering; i.e. if something has a :timestamp, that's considered before a :createdAt;
        // but a :modifiedAt is considered only AFTER we don't find e.g. an :endedAt.

        var timestamp = "https://enola.dev/timestamp";
        var created = "https://enola.dev/createdAt";
        var started = "https://enola.dev/startedAt";
        var fileCreated = "https://enola.dev/files/Node/createdAt";
        var start =
                thing.getOptional(timestamp, Instant.class)
                        .or(() -> thing.getOptional(created, Instant.class))
                        .or(() -> thing.getOptional(started, Instant.class))
                        .or(() -> thing.getOptional(fileCreated, Instant.class))
                        .orElse(Instant.MIN);

        var deleted = "https://enola.dev/deletedAt";
        var ended = "https://enola.dev/endedAt";
        var fileDeleted = "https://enola.dev/files/Node/deletedAt";
        var modified = "https://enola.dev/modifiedAt";
        var end =
                thing.getOptional(deleted, Instant.class)
                        .or(() -> thing.getOptional(ended, Instant.class))
                        .or(() -> thing.getOptional(fileDeleted, Instant.class))
                        .or(() -> thing.getOptional(modified, Instant.class))
                        .orElse(Instant.MAX);

        return List.of(Interval.of(start, end));
    }
}
