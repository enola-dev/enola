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

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.ai.iri.GoogleModelProvider.FLASH_LITE;

import com.google.adk.models.BaseLlm;

import dev.enola.ai.adk.iri.TestsLlmProvider;
import dev.enola.ai.adk.test.AgentTester;
import dev.enola.ai.iri.ModelConfig;
import dev.enola.ai.iri.Provider;
import dev.enola.common.context.TLC;

import org.junit.Test;

import java.time.Instant;
import java.time.InstantSource;
import java.time.ZoneId;
import java.util.Locale;

public class DateTimeToolsTest {

    // TODO Make the DateTimeTools reply in German, given that the test Locale is GERMANY!

    Provider<BaseLlm> llm = new TestsLlmProvider();

    Instant testInstant = Instant.parse("2025-08-14T21:05:00.00Z");
    InstantSource instantSource = InstantSource.fixed(testInstant);
    DateTimeTools dateTimeTools = new DateTimeTools(instantSource);

    @Test
    public void unit() {
        try (var ctx =
                TLC.open()
                        .push(ZoneId.class, ZoneId.of("Europe/Zurich"))
                        .push(Locale.class, Locale.GERMANY)) {
            assertThat(dateTimeTools.getCityCurrentTime("Zürich"))
                    .containsExactly(
                            "status", "success", "report", "The current time in Zürich is 23:05.");
            assertThat(dateTimeTools.getCurrentTime())
                    .containsExactly(
                            "status", "success", "report", "The current time in MEZ is 23:05.");
            assertThat(dateTimeTools.getCurrentDateAndTime())
                    .containsExactly(
                            "status",
                            "success",
                            "report",
                            "The current date & time in MEZ is Donnerstag, 14. August 2025,"
                                    + " 23:05.");
        }
    }

    @Test
    public void geminiFlashLite() {
        llm.optional(ModelConfig.temperature(FLASH_LITE, 0))
                .ifPresent(
                        model -> {
                            var agentTester =
                                    new AgentTester(
                                            model,
                                            DateTimeTools.currentDateAndTimeAdkTool(dateTimeTools),
                                            DateTimeTools.currentTimeAdkTool(dateTimeTools),
                                            DateTimeTools.cityCurrentTimeAdkTool(dateTimeTools));
                            try (var ctx =
                                    TLC.open()
                                            .push(ZoneId.class, ZoneId.of("Europe/Zurich"))
                                            .push(Locale.class, Locale.GERMANY)) {
                                agentTester.assertTextResponseEquals(
                                        "What's the time in Zürich?",
                                        "The current time in Zürich is 23:05.");
                                agentTester.assertTextResponseEquals(
                                        "What's the time?", "The current time in MEZ is 23:05.");
                                agentTester.assertTextResponseEquals(
                                        "What's today?",
                                        "The current date & time in MEZ is Donnerstag, 14. August"
                                                + " 2025, 23:05.");
                            }
                        });
    }
}
