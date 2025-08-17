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
package dev.enola.ai.mcp;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.StandardMediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.ClasspathResource;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class McpLoaderTest {

    public @Rule SingletonRule r =
            SingletonRule.$(MediaTypeProviders.set(new YamlMediaType(), new StandardMediaTypes()));

    @Test
    public void loadConfig() throws IOException {
        var r = new ClasspathResource("enola.dev/ai/mcp.yaml");
        var loader = new McpLoader();
        var config = loader.load(r);
        var everything = config.servers.get("modelcontextprotocol/everything");
        assertThat(everything.command).isEqualTo("npx");
        assertThat(everything.args)
                .containsExactly("-y", "@modelcontextprotocol/server-everything");
        assertThat(loader.names()).isNotEmpty();
    }

    @Test
    @Ignore // TODO Figure out how to make this work under Bazel... :=(
    public void createClient() throws IOException {
        var loader = new McpLoader();
        var r = new ClasspathResource("enola.dev/ai/mcp.yaml");
        var config = loader.load(r);
        assertThat(loader.names()).isNotEmpty();
        for (var name : loader.names()) {
            try (var client = loader.get(name, "Test")) {
                assertThat(client).isNotNull();
            }
        }
    }
}
