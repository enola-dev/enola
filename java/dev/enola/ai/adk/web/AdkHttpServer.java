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
import com.google.common.collect.ImmutableMap;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Extended ADK Dev Web Server.
 *
 * <p>See <a href="https://github.com/google/adk-java/issues/149">ADK issue #149</a>.
 */
public class AdkHttpServer implements AutoCloseable {

    private static @Nullable ImmutableMap<String, BaseAgent> rootAgents;

    private final ConfigurableApplicationContext springContext;
    private final int httpPort;

    private AdkHttpServer(ConfigurableApplicationContext springContext, int httpPort) {
        this.springContext = springContext;
        this.httpPort = httpPort;
    }

    public static synchronized void agents(Map<String, BaseAgent> agents) {
        if (rootAgents != null)
            throw new IllegalStateException("AdkHttpServer.agents() already set");
        rootAgents = ImmutableMap.copyOf(agents);
    }

    public static void agent(BaseAgent agent) {
        agents(ImmutableMap.of(agent.name(), agent));
    }

    public static synchronized AdkHttpServer start(int port) {
        if (rootAgents == null) throw new IllegalStateException("Call agents() before start()");

        System.setProperty("server.port", Integer.toString(port)); // Add this line

        // As in com.google.adk.web.AdkWebServer#main()
        System.setProperty(
                "org.apache.tomcat.websocket.DEFAULT_BUFFER_SIZE",
                String.valueOf(10 * 1024 * 1024));

        var app = new SpringApplication(ImprovedAdkWebServer.class);
        app.setBannerMode(Banner.Mode.OFF);
        var context = app.run();
        Environment environment = context.getBean(Environment.class);
        String httpPort = environment.getProperty("local.server.port");
        return new AdkHttpServer(context, Integer.parseInt(httpPort));
    }

    public int httpPort() {
        return httpPort;
    }

    @Override
    public void close() {
        springContext.close();
    }

    static class ImprovedAdkWebServer extends AdkWebServer {

        @Override
        public Map<String, BaseAgent> loadedAgentRegistry(
                AgentCompilerLoader loader, AgentLoadingProperties props) {
            if (rootAgents == null)
                throw new IllegalStateException("Call AdkHttpServer.agents(...) before start()");
            return rootAgents;
        }

        @Bean
        @Primary
        // Nota bene: @Primary takes precedence over the default ServerPropertiesAutoConfiguration,
        // and might therefore disable some of its functionality. Maybe we'll have to make this more
        // configurable later? TODO Perhaps with a --mode=dev|prod sort of CLI flag?
        public ServerProperties serverProperties() {
            var serverProperties = new ServerProperties();
            var errorProperties = serverProperties.getError();
            errorProperties.setIncludeStacktrace(ErrorProperties.IncludeAttribute.ALWAYS);
            errorProperties.setIncludeMessage(ErrorProperties.IncludeAttribute.ALWAYS);
            errorProperties.setIncludeException(true);
            return serverProperties;
        }
    }
}
