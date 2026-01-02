/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.convert;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import java.time.*;
import java.util.Locale;

public class TemporalAccessorToStringConverterTest {

    private void check(Instant instant, BiConverter<Instant, String> bic, String text) {
        // '\u202F' is NNBSP (non-breaking space) which some DateTimeFormatter insert
        // after time, before AM/PM and TZ (which seems weird & random; then why not everywhere?)
        assertThat(bic.convertTo(instant).replace('\u202F', ' ')).isEqualTo(text);
        assertThat(bic.convertFrom(text)).isEqualTo(instant);
    }

    @Test
    public void INSTANT_convertToFrom() {
        var localDateTime = LocalDateTime.of(2025, Month.MAY, 17, 13, 27, 34);
        var zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("America/New_York"));
        var instant = zonedDateTime.toInstant();

        check(
                instant,
                new TemporalAccessorToStringConverter<>(() -> Locale.US, () -> ZoneOffset.UTC),
                "May 17, 2025, 5:27:34 PM Z");

        check(
                instant,
                new TemporalAccessorToStringConverter<>(
                        () -> Locale.US, () -> ZoneOffset.ofHours(-4)),
                "May 17, 2025, 1:27:34 PM -04:00");

        check(
                instant,
                new TemporalAccessorToStringConverter<>(
                        () -> Locale.of("de", "ch"), () -> ZoneOffset.ofHours(-6)),
                "17. Mai 2025, 11:27:34 -06:00");

        check(
                instant,
                new TemporalAccessorToStringConverter<>(
                        () -> Locale.of("de", "ch"), () -> ZoneId.of("Europe/Zurich")),
                "17. Mai 2025, 19:27:34 MESZ");
    }

    @Test
    public void INSTANT_MIN() {
        check(
                Instant.MIN,
                new ObjectToStringWithToStringBiConverter<>(Instant.class, Instant::parse),
                "-1000000000-01-01T00:00:00Z");

        check(
                Instant.MIN,
                new TemporalAccessorToStringConverter<>(),
                "-1000000000-01-01T00:00:00Z");
    }

    @Test
    public void INSTANT_MAX() {
        check(
                Instant.MAX,
                new ObjectToStringWithToStringBiConverter<>(Instant.class, Instant::parse),
                "+1000000000-12-31T23:59:59.999999999Z");

        check(
                Instant.MAX,
                new TemporalAccessorToStringConverter<>(),
                "+1000000000-12-31T23:59:59.999999999Z");
    }

    @Test
    @Ignore // TODO FIXME PITA
    public void INSTANT_MIN1d() {
        check(
                Instant.MIN.plus(Duration.ofDays(1)),
                new ObjectToStringWithToStringBiConverter<>(Instant.class, Instant::parse),
                "-1000000000-01-02T00:00:00Z");
        check(
                Instant.MIN.plus(Duration.ofDays(1)),
                new TemporalAccessorToStringConverter<>(),
                "-1000000000-01-02T00:00:00Z");
    }
}
