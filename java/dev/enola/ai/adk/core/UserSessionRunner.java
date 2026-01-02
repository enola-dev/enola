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
import com.google.adk.agents.LiveRequestQueue;
import com.google.adk.agents.RunConfig;
import com.google.adk.events.Event;
import com.google.adk.runner.Runner;
import com.google.adk.sessions.BaseSessionService;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;

import io.reactivex.rxjava3.core.Flowable;

public class UserSessionRunner implements AutoCloseable {

    private static final RunConfig EMPTY_RUN_CONFIG = RunConfig.builder().build();

    private final Runner runner;
    private final BaseSessionService sessionService;
    private final Session session;

    public UserSessionRunner(String userId, String appName, RunnersCache runnersService) {
        this.runner = runnersService.getRunner(appName);
        this.sessionService = runner.sessionService();
        this.session = sessionService.createSession(appName, userId).blockingGet();
    }

    public UserSessionRunner(String userId, RunnersCache runnersService) {
        this(userId, singleAppName(runnersService), runnersService);
    }

    public UserSessionRunner(String userId, BaseAgent agents) {
        this(userId, new RunnersCache(agents));
    }

    private static String singleAppName(RunnersCache runnersService) {
        var appNames = runnersService.appNames();
        if (appNames.size() != 1)
            throw new IllegalArgumentException("Expected 1 app/agent only, but have: " + appNames);
        else return appNames.iterator().next();
    }

    public Flowable<Event> runAsync(Content newMessage, RunConfig runConfig) {
        return runner.runAsync(session.userId(), session.id(), newMessage, runConfig);
    }

    public Flowable<Event> runAsync(Content newMessage) {
        return this.runAsync(newMessage, EMPTY_RUN_CONFIG);
    }

    public Flowable<Event> runLive(LiveRequestQueue liveRequestQueue, RunConfig runConfig) {
        return runner.runLive(this.session, liveRequestQueue, runConfig);
    }

    public Flowable<Event> runLive(LiveRequestQueue liveRequestQueue) {
        return runner.runLive(this.session, liveRequestQueue, EMPTY_RUN_CONFIG);
    }

    @Override
    public void close() {
        sessionService.closeSession(session).blockingAwait();

        // TODO Propose deleteSession(Sesssion session)
        sessionService
                .deleteSession(session.appName(), session.userId(), session.id())
                .blockingAwait();
    }
}
