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
package dev.enola.cli.common;

import dev.enola.common.locale.LocaleSupplierTLC;
import dev.enola.common.time.ZoneIdSupplierTLC;

import picocli.CommandLine;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

public class LocaleOption {

    @CommandLine.Option(
            names = {"-l", "--locale"},
            paramLabel = "<locale>",
            description = {
                "Default Locale (AKA \"human language\" [override with ?hl= or Accept-Language on"
                        + " HTTP requests])",
                "to use for (parsing input and printing) output; e.g. `en_US` or `fr_FR`",
            })
    Locale locale;

    @CommandLine.Option(
            names = {"-tz", "--timeZone"},
            paramLabel = "<TimeZone>",
            description = {
                "Default TimeZone to use for (parsing input and printing) output; e.g."
                        + " `Europe/Zurich` or `-04:00` or `GMT`",
            })
    ZoneId zoneId;

    public void initializeSINGLETON() {
        if (locale != null) {
            LocaleSupplierTLC.SINGLETON.set(locale);
            Locale.setDefault(locale);
        } else LocaleSupplierTLC.SINGLETON.set(Locale.getDefault());

        if (zoneId != null) {
            ZoneIdSupplierTLC.SINGLETON.set(zoneId);
            TimeZone.setDefault(TimeZone.getTimeZone(zoneId));
        } else ZoneIdSupplierTLC.SINGLETON.set(ZoneId.systemDefault());
    }
}
