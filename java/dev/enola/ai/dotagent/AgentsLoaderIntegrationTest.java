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

import static dev.enola.ai.iri.GoogleModelProvider.*;

import com.google.adk.agents.BaseAgent;

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
        if (secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isEmpty()) return;
        // We must create the AgentsLoader AFTER we know that the API secret is available:
        var loader =
                new AgentsLoader(
                        rp, FLASH_LITE, new GoogleLlmProvider(secretManager), Tools.none());
        var agent = load(loader, "chef-optimist.agent.yaml");
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

        if (secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isEmpty()) return;
        var loader = new AgentsLoader(rp, FLASH, new GoogleLlmProvider(secretManager), tools);
        var agent = load(loader, "clock.agent.yaml");
        var agentTester = new AgentTester(agent);

        agentTester.assertTextResponseContainsAll(
                "What's the current date and time?",
                "The current",
                "date",
                "time",
                "Thursday, August 14, 2025, 9:05 PM");
    }

    @Test
    public void person() throws IOException {
        if (secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isEmpty()) return;
        var loader =
                new AgentsLoader(rp, FLASH, new GoogleLlmProvider(secretManager), Tools.none());
        var agent = load(loader, "person.agent.yaml");
        var agentTester = new AgentTester(agent);
        agentTester.assertJsonResponseEquals(
                "John Doe is a 35-year-old software engineer living in Zürich.",
                "{ \"name\": \"John Doe\", \"age\": 35, \"occupation\": \"software engineer\" }");
    }

    private BaseAgent load(AgentsLoader loader, String uri) throws IOException {
        var agents = loader.load(Stream.of(URI.create(uri)));
        assertThat(agents).hasSize(1);
        return agents.iterator().next();
    }
}
