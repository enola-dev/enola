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

import com.google.adk.agents.InvocationContext;
import com.google.adk.sessions.BaseSessionService;
import com.google.genai.types.Content;

import java.util.Optional;

final class InvocationContexts {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static InvocationContext replaceUserContent(
            InvocationContext ctx, Optional<Content> newUserContent) {

        BaseSessionService sessionService = ctx.sessionService();

        if (newUserContent.isEmpty()) return ctx;
        if (ctx.liveRequestQueue().isEmpty()) {
            return InvocationContext.create(
                    sessionService,
                    ctx.artifactService(),
                    ctx.invocationId(),
                    ctx.agent(),
                    ctx.session(),
                    newUserContent.get(),
                    ctx.runConfig());

        } else {
            var newCtx =
                    InvocationContext.create(
                            sessionService,
                            ctx.artifactService(),
                            ctx.agent(),
                            ctx.session(),
                            ctx.liveRequestQueue().get(),
                            ctx.runConfig());
            throw new UnsupportedOperationException(
                    "TODO How to replace new user Content with a live request queue?!");
        }
    }

    private InvocationContexts() {}
}
