/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.cli;

import dev.enola.common.convert.ObjectToStringBiConverter;
import dev.enola.common.convert.ObjectToStringBiConverters;

import picocli.CommandLine;

import java.time.Instant;

@CommandLine.Command(
        hidden = true,
        name = "test-locale",
        description = "Used only to test that Locale works as expected")
public class LocaleTestCommand implements Runnable {

    private final ObjectToStringBiConverter<Instant> converter = ObjectToStringBiConverters.INSTANT;

    @Override
    public void run() {
        System.out.println("Now is " + converter.convertTo(Instant.now()));
    }
}
