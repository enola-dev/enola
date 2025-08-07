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
package dev.enola.ai.adk.demo;

import static java.util.Objects.requireNonNull;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;
import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.FunctionTool;

import dev.enola.ai.adk.core.CLI;
import dev.enola.ai.adk.web.AdkHttpServer;
import dev.enola.ai.iri.GoogleModelProvider;
import dev.enola.common.io.iri.URIs;

import java.text.Normalizer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Demo using <a
 * href="https://google.github.io/adk-docs/get-started/quickstart/#create-multitoolagentjava">Quickstart</a>
 * from ADK docs.
 */
public class QuickstartDemo {

    // TODO Modularize this better...

    public static final String NYC_WEATHER =
            "The weather in New York is sunny with a temperature of 25 degrees Celsius (77"
                    + " degrees Fahrenheit).";

    public static BaseAgent initAgent() {
        return initAgent(System.getenv("GOOGLE_API_KEY"));
    }

    public static BaseAgent initAgent(String apiKey) {
        var model = URIs.getQueryMap(GoogleModelProvider.FLASH_LITE).get("model");
        return initAgent(new Gemini(requireNonNull(model), apiKey));
    }

    public static BaseAgent initAgent(BaseLlm baseLLM) {
        return LlmAgent.builder()
                .name("multi_tool_agent")
                .model(baseLLM)
                .description("Agent to answer questions about the time and weather in a city.")
                .instruction(
                        "You are a helpful agent who can answer user questions about the time and"
                                + " weather in a city.")
                .tools(
                        // TODO Create separate Agent classes
                        FunctionTool.create(QuickstartDemo.class, "getCurrentTime"),
                        FunctionTool.create(QuickstartDemo.class, "getWeather"))
                .build();
    }

    public static Map<String, String> getCurrentTime(
            @Schema(description = "The name of the city for which to retrieve the current time")
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

    public static Map<String, String> getWeather(
            @Schema(description = "The name of the city for which to retrieve the weather report")
                    String city) {
        if (city.equalsIgnoreCase("new york")) {
            return Map.of("status", "success", "report", NYC_WEATHER);

        } else {
            return Map.of(
                    "status",
                    "error",
                    "report",
                    "Weather information for " + city + " is not available.");
        }
    }

    public static void main(String[] args) {
        var agent = initAgent();

        AdkHttpServer.start(agent, 8080);
        System.out.println("Web UI started on http://localhost:8080");

        CLI.main(args, agent);
    }
}
