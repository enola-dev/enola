/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.ai.adk.tool.builtin;

import static dev.enola.common.SuccessOrError.error;
import static dev.enola.common.SuccessOrError.success;

import static java.lang.String.format;

import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;

import dev.enola.ai.adk.tool.Tools;
import dev.enola.common.SuccessOrError;
import dev.enola.common.locale.LocaleSupplier;
import dev.enola.common.locale.LocaleSupplierTLC;
import dev.enola.common.time.ZoneIdSupplier;
import dev.enola.common.time.ZoneIdSupplierTLC;

import java.text.Normalizer;
import java.time.InstantSource;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

public final class DateTimeTools {

    // TODO Merge cityCurrentTime & currentTime by making City optional

    // TODO Support "What's the time in Lausanne?" Does it suffice to use Pro instead of Flash-Lite?

    public static final BaseTool CITY_TIME =
            cityCurrentTimeAdkTool(new DateTimeTools(InstantSource.system()));

    public static BaseTool currentDateAndTimeAdkTool(DateTimeTools dateTimeTool) {
        return FunctionTool.create(dateTimeTool, "getCurrentDateAndTime");
    }

    public static BaseTool currentTimeAdkTool(DateTimeTools dateTimeTool) {
        return FunctionTool.create(dateTimeTool, "getCurrentTime");
    }

    public static BaseTool cityCurrentTimeAdkTool(DateTimeTools dateTimeTool) {
        return FunctionTool.create(dateTimeTool, "getCityCurrentTime");
    }

    private final InstantSource instantSource;
    private final ZoneIdSupplier zoneIdSupplier;
    private final LocaleSupplier localeSupplier;

    public DateTimeTools(
            InstantSource instantSource,
            ZoneIdSupplier zoneIdSupplier,
            LocaleSupplier localeSupplier) {
        this.instantSource = instantSource;
        this.zoneIdSupplier = zoneIdSupplier;
        this.localeSupplier = localeSupplier;
    }

    public DateTimeTools(InstantSource instantSource) {
        this(instantSource, ZoneIdSupplierTLC.UTC, new LocaleSupplierTLC(Locale.ENGLISH));
    }

    @Schema(description = "Returns the current date and time")
    public Map<String, ?> getCurrentDateAndTime() {
        var locale = localeSupplier.get();
        var zid = zoneIdSupplier.get();
        var zidLabel = zid.getDisplayName(TextStyle.SHORT_STANDALONE, locale);
        return Tools.toMap(
                formatTimeForZone(zidLabel, zid.getId(), dateTimeFormatter(), "date & time"));
    }

    @Schema(description = "Returns the current time")
    public Map<String, ?> getCurrentTime() {
        var locale = localeSupplier.get();
        var zid = zoneIdSupplier.get();
        var zidLabel = zid.getDisplayName(TextStyle.SHORT_STANDALONE, locale);
        return Tools.toMap(formatTimeForZone(zidLabel, zid.getId(), timeFormatter(), "time"));
    }

    @Schema(description = "Returns the current time in the given city")
    public Map<String, ?> getCityCurrentTime(
            @Schema(description = "The name of the city for which to retrieve the current time")
                    String city) {
        return Tools.toMap(cityCurrentTime(city));
    }

    private SuccessOrError<String> cityCurrentTime(String city) {
        var normalizedCity =
                Normalizer.normalize(city, Normalizer.Form.NFD)
                        .trim()
                        .toLowerCase()
                        .replaceAll("(\\p{IsM}+|\\p{IsP}+)", "")
                        .replaceAll("\\s+", "_");

        return ZoneId.getAvailableZoneIds().stream()
                .filter(zid -> zid.toLowerCase().endsWith("/" + normalizedCity))
                .findFirst()
                .map(zid -> formatTimeForZone(city, zid, timeFormatter(), "time"))
                .orElse(error("Sorry, I don't have timezone information for " + city + "."));
    }

    private DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT)
                .withLocale(localeSupplier.get());
    }

    private DateTimeFormatter timeFormatter() {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(localeSupplier.get());
    }

    private SuccessOrError<String> formatTimeForZone(
            String city, String zid, DateTimeFormatter formatter, String label) {
        var now = instantSource.instant();
        var zoneId = ZoneId.of(zid);
        var time = ZonedDateTime.ofInstant(now, zoneId).format(formatter);

        // TODO Message Bundle (?) to translate - or could/should we let the LLM do this?!
        return success(format("The current %s in %s is %s.", label, city, time));
    }
}
