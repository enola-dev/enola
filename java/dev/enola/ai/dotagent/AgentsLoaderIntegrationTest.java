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

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.ai.iri.GoogleModelProvider.FLASH_LITE;
import static dev.enola.ai.iri.GoogleModelProvider.GOOGLE_AI_API_KEY_SECRET_NAME;

import static java.util.Collections.emptyMap;

import dev.enola.ai.adk.iri.GoogleLlmProvider;
import dev.enola.ai.adk.test.AgentTester;
import dev.enola.ai.adk.tool.Tools;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.StandardMediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.auto.TestSecretManager;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.InstantSource;
import java.util.stream.Stream;

public class AgentsLoaderIntegrationTest {

    public @Rule SingletonRule r =
            SingletonRule.$(MediaTypeProviders.set(new YamlMediaType(), new StandardMediaTypes()));

    final SecretManager secretManager = new TestSecretManager();
    final ResourceProvider rp = new ClasspathResource.Provider("agents");

    @Test
    public void optimisticChefYAML() throws IOException {
        if (!secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent()) return;
        // We must create the AgentsLoader AFTER we know that the API secret is available:
        var loader =
                new AgentsLoader(rp, FLASH_LITE, new GoogleLlmProvider(secretManager), emptyMap());

        var agents = loader.load(Stream.of(URI.create("chef-optimist.agent.yaml")));
        assertThat(agents).hasSize(1);
        var agent = agents.iterator().next();

        var agentTester = new AgentTester(agent);
        agentTester.assertTextResponseContainsAny(
                "How to make scrambled eggs?",
                "darling",
                "epiphany",
                "odyssey",
                "sunshine",
                "symphony",
                "precious",
                "jewels",
                "bliss");
    }

    @Test
    public void clock() throws IOException {
        Instant testInstant = Instant.parse("2025-08-14T21:05:00.00Z");
        InstantSource testInstantSource = InstantSource.fixed(testInstant);
        var tools = Tools.builtin(testInstantSource);

        if (!secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent()) return;
        var loader = new AgentsLoader(rp, FLASH_LITE, new GoogleLlmProvider(secretManager), tools);

        var agents = loader.load(Stream.of(URI.create("clock.agent.yaml")));
        assertThat(agents).hasSize(1);
        var agent = agents.iterator().next();

        var agentTester = new AgentTester(agent);
        agentTester.assertTextResponseContainsAll(
                "What's the current date and time?",
                "The current date and time is",
                "Thursday, August 14, 2025, 9:05 PM");
    }
}
