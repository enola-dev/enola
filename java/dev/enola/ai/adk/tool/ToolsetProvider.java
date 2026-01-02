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

import com.google.adk.tools.BaseTool;
import com.google.adk.tools.BaseToolset;
import com.google.common.collect.ImmutableMap;

import dev.enola.common.name.NamedTypedObjectProvider;

import java.util.Map;

public interface ToolsetProvider extends NamedTypedObjectProvider<BaseToolset> {

    static ToolsetProvider immutableToolsets(Map<String, BaseToolset> map) {
        return new ImmutableToolsetProvider(map);
    }

    static ToolsetProvider immutableTools(Map<String, BaseTool> map) {
        var builder = ImmutableMap.<String, BaseToolset>builder();
        for (var entry : map.entrySet()) {
            builder.put(entry.getKey(), new ToolToolset(entry.getValue()));
        }
        return new ImmutableToolsetProvider(builder.buildOrThrow());
    }
}
