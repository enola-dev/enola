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

import static dev.enola.ai.iri.GoogleModelProvider.FLASH;

import com.google.adk.models.BaseLlm;

import dev.enola.ai.adk.iri.TestsLlmProvider;
import dev.enola.ai.adk.test.AgentTester;
import dev.enola.ai.iri.Provider;
import dev.enola.common.context.TLC;

import org.junit.Test;

import java.time.Instant;
import java.time.InstantSource;
import java.time.ZoneId;

public class DateTimeToolsTest {

    Provider<BaseLlm> llm = new TestsLlmProvider();

    @Test
    public void unit() {
        var testInstant = Instant.parse("2025-08-14T21:05:00.00Z");
        var instantSource = InstantSource.fixed(testInstant);
        var dateTimeTools = new DateTimeTools(instantSource);
        assertThat(dateTimeTools.getCityCurrentTime("Zürich"))
                .containsExactly(
                        "status", "success", "report", "The current time in Zürich is 23:05.");
        assertThat(dateTimeTools.getCurrentTime())
                .containsExactly("status", "success", "report", "The current time in Z is 21:05.");
        try (var ctx = TLC.open().push(ZoneId.class, ZoneId.of("Europe/Zurich"))) {
            assertThat(dateTimeTools.getCurrentTime())
                    .containsExactly(
                            "status", "success", "report", "The current time in CET is 23:05.");
        }
    }

    @Test
    public void geminiFlashLite() {
        llm.optional(FLASH)
                .ifPresent(
                        model -> {
                            var agentTester =
                                    new AgentTester(
                                            model, DateTimeTools.TIME, DateTimeTools.CITY_TIME);
                            agentTester.assertTextResponseContains(
                                    "What's the time in Zürich?", "The current time in Zürich is ");
                            try (var ctx =
                                    TLC.open().push(ZoneId.class, ZoneId.of("Europe/Zurich"))) {
                                agentTester.assertTextResponseContains(
                                        "What's the time?", "The current time in CET is ");
                            }
                        });
    }
}
