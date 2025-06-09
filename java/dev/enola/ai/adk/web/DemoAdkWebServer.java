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
import com.google.adk.web.AdkWebServer;
import com.google.adk.web.AgentCompilerLoader;
import com.google.adk.web.config.AgentLoadingProperties;

import dev.enola.ai.adk.core.MockAgent;
import dev.enola.ai.adk.core.QuickstartDemo;

import org.springframework.boot.SpringApplication;

import java.util.Map;

/**
 * Extended ADK Dev Web Server.
 *
 * <p>See <a href="https://github.com/google/adk-java/issues/149">ADK issue #149</a>.
 */
public class DemoAdkWebServer extends AdkWebServer {

    public static AutoCloseable start(int port) {
        System.setProperty("server.port", Integer.toString(port)); // Add this line

        // As in com.google.adk.web.AdkWebServer#main()
        // TODO Avoid copy/paste by making this re-usable in a (static) method AdkWebServer
        System.setProperty(
                "org.apache.tomcat.websocket.DEFAULT_BUFFER_SIZE",
                String.valueOf(10 * 1024 * 1024));

        return SpringApplication.run(DemoAdkWebServer.class);
    }

    @Override
    public Map<String, BaseAgent> loadedAgentRegistry(
            AgentCompilerLoader loader, AgentLoadingProperties props) {
        var root =
                System.getenv("GOOGLE_API_KEY") != null
                        ? QuickstartDemo.initAgent()
                        : new MockAgent();
        return Map.of(root.name(), root);
    }

    public static void main(String[] args) {
        DemoAdkWebServer.start(8080);
    }
}
