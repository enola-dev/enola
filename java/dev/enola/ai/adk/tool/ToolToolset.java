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
package dev.enola.ai.adk.tool;

import com.google.adk.agents.ReadonlyContext;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.BaseToolset;

import io.reactivex.rxjava3.core.Flowable;

class ToolToolset implements BaseToolset {

    private final BaseTool tool;

    ToolToolset(BaseTool tool) {
        this.tool = tool;
    }

    @Override
    public Flowable<BaseTool> getTools(ReadonlyContext readonlyContext) {
        return Flowable.just(tool);
    }

    @Override
    public void close() {}
}
