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
package dev.enola.ai.tool;

import com.google.adk.tools.BaseTool;
import com.google.adk.tools.ToolContext;
import com.google.genai.types.FunctionDeclaration;

import io.reactivex.rxjava3.core.Single;

import java.util.Map;
import java.util.Optional;

// TODO This is archived / parked here, just for potential future use; currently not built/used.
public class ToolAdapter extends BaseTool {

    public ToolAdapter(Tool tool) {
        this(tool.name(), tool.description(), false);
    }

    public ToolAdapter(AsyncTool tool) {
        this(tool.name(), tool.description(), false);
    }

    private ToolAdapter(String name, String description, boolean isLongRunning) {
        super(name, description, isLongRunning);
    }

    @Override
    public Optional<FunctionDeclaration> declaration() {
        throw new UnsupportedOperationException("TODO Implement this method.");
    }

    @Override
    public Single<Map<String, Object>> runAsync(Map<String, Object> args, ToolContext toolContext) {
        throw new UnsupportedOperationException("TODO Implement this method.");
    }
}
