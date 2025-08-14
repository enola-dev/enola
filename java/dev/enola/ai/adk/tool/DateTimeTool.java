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

import com.google.adk.tools.Annotations;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;

import java.text.Normalizer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

// TODO WIP...
public class DateTimeTool extends BaseTool {

    public static final BaseTool INSTANCE =
            FunctionTool.create(new DateTimeTool(), "getCurrentTime");

    public Map<String, String> getCurrentTime(
            @Annotations.Schema(
                            description =
                                    "The name of the city for which to retrieve the current time")
                    String city) {
        String normalizedCity =
                Normalizer.normalize(city, Normalizer.Form.NFD)
                        .trim()
                        .toLowerCase()
                        .replaceAll("(\\p{IsM}+|\\p{IsP}+)", "")
                        .replaceAll("\\s+", "_");

        return ZoneId.getAvailableZoneIds().stream()
                .filter(zid -> zid.toLowerCase().endsWith("/" + normalizedCity))
                .findFirst()
                .map(
                        zid ->
                                Map.of(
                                        "status",
                                        "success",
                                        "report",
                                        "The current time in "
                                                + city
                                                + " is "
                                                + ZonedDateTime.now(ZoneId.of(zid))
                                                        .format(
                                                                DateTimeFormatter.ofPattern(
                                                                        "HH:mm"))
                                                + "."))
                .orElse(
                        Map.of(
                                "status",
                                "error",
                                "report",
                                "Sorry, I don't have timezone information for " + city + "."));
    }

    public DateTimeTool() {
        super("Current Date & Time", "Returns the current date and time.");
    }
}
