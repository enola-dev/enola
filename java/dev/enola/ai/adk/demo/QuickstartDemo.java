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
package dev.enola.ai.adk.demo;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.FunctionTool;

import dev.enola.ai.adk.core.CLI;
import dev.enola.ai.adk.iri.LlmProviders;
import dev.enola.ai.adk.tool.Tools;
import dev.enola.ai.adk.tool.builtin.DateTimeTools;
import dev.enola.ai.adk.web.AdkHttpServer;
import dev.enola.ai.iri.GoogleModelProvider;
import dev.enola.common.secret.auto.AutoSecretManager;

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

    public static BaseAgent initAgent(BaseLlm baseLLM) {
        return LlmAgent.builder()
                .name("multi_tool_agent")
                .model(baseLLM)
                .description("Agent to answer questions about the time and weather in a city.")
                .instruction(
                        "You are a helpful agent who can answer user questions about the time and"
                                + " weather in a city.")
                .tools(
                        DateTimeTools.CITY_TIME,
                        FunctionTool.create(QuickstartDemo.class, "getWeather"))
                .build();
    }

    public static Map<String, ?> getWeather(
            @Schema(description = "The name of the city for which to retrieve the weather report")
                    String city) {
        if ("new york".equalsIgnoreCase(city)) {
            return Tools.successMap(NYC_WEATHER);
        } else {
            return Tools.errorMap("Weather information for " + city + " is not available.");
        }
    }

    public static void main(String[] args) {
        var provider = new LlmProviders(AutoSecretManager.INSTANCE());
        var model = provider.get(GoogleModelProvider.FLASH_LITE);
        var agent = initAgent(model);

        AdkHttpServer.start(agent, 8080);
        System.out.println("Web UI started on http://localhost:8080");

        CLI.main(args, agent);
    }
}
