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
package dev.enola.ai.dotagent;

import com.google.adk.models.BaseLlm;

import dev.enola.ai.adk.tool.ToolsetProvider;
import dev.enola.ai.iri.Provider;
import dev.enola.common.io.resource.ResourceProvider;

import java.io.IOException;
import java.net.URI;
import java.util.stream.Stream;

public class AgentsTester {

    public AgentsTester(
            ResourceProvider resourceProvider,
            URI defaultLLM,
            Provider<BaseLlm> llmProvider,
            ToolsetProvider toolsProvider) {}

    public void test(String url) throws IOException {
        test(Stream.of(URI.create(url)));
    }

    public void test(Stream<URI> urls) throws IOException {}
}
