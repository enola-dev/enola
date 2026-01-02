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
package dev.enola.ai.adk.core;

import com.google.adk.agents.BaseAgent;
import com.google.adk.artifacts.BaseArtifactService;
import com.google.adk.artifacts.InMemoryArtifactService;
import com.google.adk.runner.Runner;
import com.google.adk.sessions.BaseSessionService;
import com.google.adk.sessions.InMemorySessionService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Copy/paste from com.google.adk.web.AdkWebServer#RunnerService, with the following changes:
//   * De-Spring-ified by removing @Component @Autowired @Qualifier("loadedAgentRegistry")
//   * getRunner() IllegalArgumentException instead of ResponseStatusException(HttpStatus.NOT_FOUND)
//   * Added additional convenience constructors
public class RunnersCache {

    private static final Logger log = LoggerFactory.getLogger(RunnersCache.class);

    private final Map<String, BaseAgent> agentRegistry;
    private final BaseArtifactService artifactService;
    private final BaseSessionService sessionService;
    private final Map<String, Runner> runnerCache = new ConcurrentHashMap<>();

    public RunnersCache(
            Map<String, BaseAgent> agentRegistry,
            BaseArtifactService artifactService,
            BaseSessionService sessionService) {
        this.agentRegistry = ImmutableMap.copyOf(agentRegistry); // skipcq: JAVA-E1086
        this.artifactService = artifactService;
        this.sessionService = sessionService;
    }

    public RunnersCache(Map<String, BaseAgent> agentRegistry) {
        this(agentRegistry, new InMemoryArtifactService(), new InMemorySessionService());
    }

    public RunnersCache(Iterable<BaseAgent> agents) {
        this(Agents.toMap(agents));
    }

    public RunnersCache(BaseAgent agents) {
        this(Agents.toMap(ImmutableList.of(agents)));
    }

    public Set<String> appNames() {
        return agentRegistry.keySet();
    }

    /**
     * Gets the Runner instance for a given application name. Handles potential agent engine ID
     * overrides.
     *
     * @param appName The application name requested by the user.
     * @return A configured Runner instance.
     * @throws IllegalArgumentException If no agent matching appName found in the agent registry.
     */
    public Runner getRunner(String appName) throws IllegalArgumentException {
        return runnerCache.computeIfAbsent(
                appName,
                key -> {
                    BaseAgent agent = agentRegistry.get(key);
                    if (agent == null) {
                        log.error(
                                "Agent/App named '{}' not found in registry. Available apps: {}",
                                key,
                                agentRegistry.keySet());
                        throw new IllegalArgumentException("Agent/App not found: " + key);
                    }
                    log.info(
                            "RunnerService: Creating Runner for appName: {}, using agent"
                                    + " definition: {}",
                            appName,
                            agent.name());
                    return new Runner(agent, appName, this.artifactService, this.sessionService);
                });
    }
}
