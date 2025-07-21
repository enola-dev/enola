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
package dev.enola.ai.adk.web;

import com.google.adk.agents.BaseAgent;

import dev.enola.ai.adk.core.QuickstartDemo;
import dev.enola.ai.adk.test.MockAgent;

import java.util.Map;

public class QuickstartDemoWebMain {

    static Map<String, BaseAgent> demo() {
        var root =
                System.getenv("GOOGLE_API_KEY") != null
                        ? QuickstartDemo.initAgent()
                        : new MockAgent("bar");
        return Map.of(root.name(), root);
    }

    public static void main(String[] args) {
        AdkHttpServer.agents(demo());
        AdkHttpServer.start(8080);
    }
}
