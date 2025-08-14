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
package dev.enola.ai.adk.tool;

import static dev.enola.common.SuccessOrError.error;
import static dev.enola.common.SuccessOrError.success;

import static java.lang.String.format;

import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;

import dev.enola.common.SuccessOrError;

import java.text.Normalizer;
import java.time.InstantSource;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DateTimeTool {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static final BaseTool INSTANCE = asAdkTool(new DateTimeTool(InstantSource.system()));

    public static BaseTool asAdkTool(DateTimeTool dateTimeTool) {
        return FunctionTool.create(dateTimeTool, "getCurrentTime");
    }

    private final InstantSource instantSource;

    public DateTimeTool(InstantSource instantSource) {
        this.instantSource = instantSource;
    }

    @Schema(description = "Returns the current time in the given city")
    public Map<String, String> getCurrentTime(
            @Schema(description = "The name of the city for which to retrieve the current time")
                    String city) {
        return Tools.toMap(currentTime(city));
    }

    private SuccessOrError<String> currentTime(String city) {
        String normalizedCity =
                Normalizer.normalize(city, Normalizer.Form.NFD)
                        .trim()
                        .toLowerCase()
                        .replaceAll("(\\p{IsM}+|\\p{IsP}+)", "")
                        .replaceAll("\\s+", "_");

        return ZoneId.getAvailableZoneIds().stream()
                .filter(zid -> zid.toLowerCase().endsWith("/" + normalizedCity))
                .findFirst()
                .map(zid -> formatTimeForZone(city, zid))
                .orElse(error("Sorry, I don't have timezone information for " + city + "."));
    }

    private SuccessOrError<String> formatTimeForZone(String city, String zid) {
        var now = instantSource.instant();
        var zoneId = ZoneId.of(zid);
        var time = ZonedDateTime.ofInstant(now, zoneId).format(FORMATTER);
        return success(format("The current time in %s is %s.", city, time));
    }
}
