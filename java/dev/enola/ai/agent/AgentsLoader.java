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
package dev.enola.ai.agent;

import dev.enola.ai.dotprompt.DotPromptLoader;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.ResourceProvider;

import java.io.IOException;
import java.net.URI;

public class AgentsLoader {

    private final DotPromptLoader dotPromptLoader;

    public AgentsLoader(ResourceProvider resourceProvider, URI defaultLLM) {
        this.dotPromptLoader = new DotPromptLoader(resourceProvider, defaultLLM);
    }

    public AgentsModel load(URI uri) throws IOException {
        if (uri.getPath().endsWith(".prompt")) {
            var loadedDotPrompt = dotPromptLoader.load(uri);
            // TODO loadedDotPrompt.
        }
        if (uri.getPath().endsWith(".agents.yaml")) {}

        throw new IllegalArgumentException("Unknown extension on URI: " + uri);
    }
}
