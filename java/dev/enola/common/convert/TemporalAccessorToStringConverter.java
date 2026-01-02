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

import com.google.common.collect.ImmutableList;

import dev.enola.common.locale.LocaleSupplier;
import dev.enola.common.locale.LocaleSupplierTLC;
import dev.enola.common.time.ZoneIdSupplier;
import dev.enola.common.time.ZoneIdSupplierTLC;

import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public class TemporalAccessorToStringConverter<T extends TemporalAccessor>
        implements BiConverter<T, String>, ObjectToStringBiConverter<T> {

    // TODO Factor common.convert unrelated parts of this out into common.time

    // TODO Test (and likely fix resulting bugs from) other TemporalAccessor than Instant

    public static final ObjectToStringBiConverter<Instant> INSTANT =
            new TemporalAccessorToStringConverter<>();

    private final String INSTANT_MIN_TEXT = Instant.MIN.toString();
    private final String INSTANT_MAX_TEXT = Instant.MAX.toString();

    private final LocaleSupplier localeSupplier;
    private final ZoneIdSupplier timezoneSupplier;
    private final ImmutableList<DateTimeFormatter> formatters;

    public TemporalAccessorToStringConverter(
            LocaleSupplier localeSupplier,
            ZoneIdSupplier timezoneSupplier,
            Iterable<DateTimeFormatter> formatters) {
        this.localeSupplier = localeSupplier;
        this.timezoneSupplier = timezoneSupplier;
        this.formatters = ImmutableList.copyOf(formatters);
    }

    public TemporalAccessorToStringConverter(
            LocaleSupplier localeSupplier, ZoneIdSupplier timezoneSupplier) {
        this(
                localeSupplier,
                timezoneSupplier,
                ImmutableList.of(
                        DateTimeFormatter.ISO_INSTANT,
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL),
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG),
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM),
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT),
                        // TODO This seems stupid - is there really no better way to do this?!
                        DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm:ss a z"),
                        DateTimeFormatter.ofPattern("dd. MMMM yyyy, HH:mm:ss XXX"),
                        DateTimeFormatter.ofPattern("dd. MMMM yyyy, HH:mm:ss z")
                        // ...
                        ));
    }

    public TemporalAccessorToStringConverter() {
        // NOT this(LocaleSupplierTLC.JVM_DEFAULT, TimezoneSupplierTLC.JVM_DEFAULT);
        // but intentionally use something stable which does not change, for unflaky tests;
        // this is fine, because real applications (CLI, webapps, etc.) should always have
        // another Locale and Timezone set in the TLC anyways.
        this(LocaleSupplierTLC.ROOT, ZoneIdSupplierTLC.UTC);
    }

    @Override
    public @Nullable String convertTo(@Nullable T input) throws ConversionException {
        if (input == null) return null;
        TemporalAccessor temporalAccessor = input;

        // Special handling of MIN & MAX using the default ISO_INSTANT DateTimeFormatter
        if (input.equals(Instant.MIN)) return INSTANT_MIN_TEXT;
        if (input.equals(Instant.MAX)) return INSTANT_MAX_TEXT;

        var tz = timezoneSupplier.get();
        if (temporalAccessor instanceof Instant instant) {
            temporalAccessor = instant.atZone(tz);
        }

        DateTimeFormatter formatter;
        var locale = localeSupplier.get();
        if (locale.equals(Locale.ROOT)) formatter = DateTimeFormatter.ISO_INSTANT;
        else
            formatter =
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
                            .withLocale(locale)
                            .withZone(tz);
        return formatter.format(temporalAccessor);
    }

    @Override
    @SuppressWarnings("unchecked") // Java generics are stupid
    public @Nullable T convertFrom(@Nullable String text) throws ConversionException {
        if (text == null) return null;

        // Special handling of MIN & MAX using the default ISO_INSTANT DateTimeFormatter
        if (text.equals(INSTANT_MIN_TEXT)) return (T) Instant.MIN;
        if (text.equals(INSTANT_MAX_TEXT)) return (T) Instant.MAX;

        try {
            return (T) parseTryingPatterns(text);
        } catch (DateTimeParseException e) {
            throw new ConversionException(text, e);
        }
    }

    private TemporalAccessor parseTryingPatterns(String text) {
        for (var formatter : formatters) {
            try {
                return parse(text, formatter);
            } catch (DateTimeParseException e) {
                // throw e; // For easier DEBUGGING (only)
                // Ignore
            }
        }
        throw new DateTimeParseException(
                "Could not parse with any of the registered DateTimeFormatter", text, 0);
    }

    private TemporalAccessor parse(String text, DateTimeFormatter formatter) {
        var localizedZonedFormatter =
                formatter
                        .withLocale(localeSupplier.get())
                        .withZone(timezoneSupplier.get())
                        .withResolverStyle(ResolverStyle.SMART); // TODO STRICT ?
        // NB: Must use ZonedDateTime instead of OffsetDateTime, because the former parses both
        // "-06:00" (offset) as well as "MESZ" (zone) whereas the latter only handles offsets.
        var offsetDateTime = ZonedDateTime.parse(text, localizedZonedFormatter);
        return offsetDateTime.toInstant();
    }
}
