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
package dev.enola.common.time;

import static java.util.Objects.requireNonNull;

import com.google.errorprone.annotations.Immutable;

import java.time.Instant;

/**
 * Interval of time on the time-line from a start to an end instant.
 *
 * <p>The start is inclusive, and the end exclusive ("half-open").
 *
 * <p>The end instant is always greater than or equal to the start instant.
 *
 * <p>Intervals are intentionally not {@link Comparable}.
 *
 * <p>This is inspired by:
 *
 * <ul>
 *   <li><a href="https://en.wikipedia.org/wiki/ISO_8601#Time_intervals">ISO 8601 Time Intervals</a>
 *   <li><a
 *       href="https://www.threeten.org/threeten-extra/apidocs/org.threeten.extra/org/threeten/extra/Interval.html">
 *       <code>org.threeten.extra.Interval</code></a>
 *   <li><a href="https://www.joda.org/joda-time/key_interval.html"><code>org.joda.time.Interval
 *       </code></a>
 * </ul>
 *
 * @param start the start instant, inclusive
 * @param end the end instant, exclusive
 */
@Immutable
public record Interval(Instant start, Instant end) {

    // TODO Write a parse() and an ObjectToStringBiConverters for this
    // https://github.com/ThreeTen/threeten-extra/blob/4e016340b97cab604114d10e02a672c1e94c6be5/src/main/java/org/threeten/extra/Interval.java#L201

    /** An interval over the whole time-line. */
    public static final Interval ALL = new Interval(Instant.MIN, Instant.MAX);

    /**
     * Obtains an instance of Interval from the start and end instant.
     *
     * @param startInclusive the start instant, inclusive, {@link Instant#MIN} treated as unbounded,
     *     not null
     * @param endExclusive the end instant, exclusive, {@link Instant#MAX} treated as unbounded, not
     *     null
     * @return the half-open interval, not null
     */
    public static Interval of(Instant startInclusive, Instant endExclusive) {
        if (Instant.MIN.equals(startInclusive) && Instant.MAX.equals(endExclusive)) return ALL;
        return new Interval(startInclusive, endExclusive);
    }

    /**
     * Obtains an instance of Interval with the specified start instant and unbounded end.
     *
     * @param startInclusive the start instant, inclusive, not null
     * @return a new Instant with the specified start instant, not null
     */
    public static Interval startingAt(Instant startInclusive) {
        if (Instant.MIN.equals(startInclusive)) return ALL;
        return new Interval(startInclusive, Instant.MAX);
    }

    /**
     * Obtains an instance of Interval with unbounded start and the specified end instant.
     *
     * @param endExclusive the end instant, exclusive, not null
     * @return a new Instant with the specified end instant, not null
     */
    public static Interval endAt(Instant endExclusive) {
        if (Instant.MAX.equals(endExclusive)) return ALL;
        return new Interval(Instant.MIN, endExclusive);
    }

    /**
     * Constructor, identical to {@link #of(Instant, Instant)}.
     *
     * @deprecated The static factory method should be preferred.
     */
    public @Deprecated Interval(Instant start, Instant end) {
        this.start = requireNonNull(start, "start");
        this.end = requireNonNull(end, "end");
        if (end.isBefore(start))
            throw new IllegalArgumentException(
                    start + "start must be before (or equal to) end" + end);
    }

    /**
     * Gets the start of this time interval, inclusive.
     *
     * <p>This will return {@link Instant#MIN} if the range is unbounded at the start. In this case,
     * the range includes all dates into the far-past.
     *
     * @return the start of the time interval, inclusive, not null
     */
    public Instant start() {
        return start;
    }

    /**
     * Gets the end of this time interval, exclusive.
     *
     * <p>This will return {@link Instant#MAX} if the range is unbounded at the end. In this case,
     * the range includes all dates into the far-future.
     *
     * @return the end of the time interval, exclusive, not null
     */
    public Instant end() {
        return end;
    }

    /**
     * Checks if the start of the interval is unbounded (i.e. equals {@link Instant#MIN}).
     *
     * @return true if start is unbounded
     */
    public boolean isUnboundedStart() {
        return start.equals(Instant.MIN);
    }

    /**
     * Checks if the end of the interval is unbounded (i.e. equals {@link Instant#MAX}).
     *
     * @return true if end is unbounded
     */
    public boolean isUnboundedEnd() {
        return end.equals(Instant.MAX);
    }

    /**
     * Checks if the range is empty.
     *
     * <p>An empty range occurs when the start date equals the inclusive end date.
     *
     * @return true if the range is empty
     */
    public boolean isEmpty() {
        return start.equals(end);
    }

    // NB: No custom equals() and hashCode() because record defines that.

    /**
     * Outputs this interval as a {@code String} in ISO-8601 compliant format, such as e.g. {@code
     * 2007-12-03T10:15:30/2007-12-04T10:15:30}.
     *
     * <p>An {@link Interval#ALL} is printed as
     * -1000000000-01-01T00:00:00Z/+1000000000-12-31T23:59:59.999999999Z; other Intervals with an
     * unbounded start or end date similarly.
     *
     * @return a string representation of this instant, not null
     */
    @Override
    public String toString() {
        return start + "/" + end;
    }
}
