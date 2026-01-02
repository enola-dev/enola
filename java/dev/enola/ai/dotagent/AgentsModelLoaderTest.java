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
package dev.enola.ai.dotagent;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.StandardMediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ResourceProvider;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class AgentsModelLoaderTest {

    public @Rule SingletonRule r =
            SingletonRule.$(MediaTypeProviders.set(new YamlMediaType(), new StandardMediaTypes()));

    final ResourceProvider rp = new ClasspathResource.Provider("agents");
    final AgentsModelLoader aml = new AgentsModelLoader(rp);

    @Test
    public void clock() throws IOException {
        var clockAgentModel = aml.load(URI.create("clock.agent.yaml"));
        var aClockAgent = clockAgentModel.agents.iterator().next();
        assertThat(aClockAgent.description)
                .isEqualTo("Agent to answer questions about the current time.");
        assertThat(aClockAgent.tools).containsExactly("clock");
    }

    @Test
    public void optimisticChefYAML() throws IOException {
        checkOptimisticChef(aml.load(URI.create("chef-optimist.agent.yaml")));
    }

    @Test
    public void optimisticChefJSON() throws IOException {
        checkOptimisticChef(aml.load(URI.create("chef-optimist.agent.json")));
    }

    @Test // The goal of this is to make sure newline processing works as intended
    public void optimisticChefJSONYAML() throws IOException {
        var fromJSON = aml.load(URI.create("chef-optimist.agent.json"));
        var instructionJSON = fromJSON.agents.iterator().next().instruction;

        var fromYAML = aml.load(URI.create("chef-optimist.agent.yaml"));
        var instructionYAML = fromYAML.agents.iterator().next().instruction;

        assertThat(instructionJSON).isEqualTo(instructionYAML);
    }

    @Test
    public void oppositeChefsStream() throws IOException {
        checkOppositeChefs(aml.load(URI.create("chefs-opposites-stream.agent.yaml")));
    }

    @Test
    @Ignore // TODO
    public void oppositeChefsMap() throws IOException {
        checkOppositeChefs(aml.load(URI.create("chefs-opposites-map.agent.yaml")));
    }

    private void checkOptimisticChef(AgentsModel agentsModel) {
        assertThat(agentsModel.agents).hasSize(1);
        var agentModel = agentsModel.agents.stream().findFirst().get();
        assertThat(agentModel.instruction)
                .startsWith("You are an unwaveringly optimistic and passionate chef who");
        assertThat(agentModel.name).isEqualTo("chef-optimist");
        assertThat(agentModel.id).isNotNull();
    }

    private void checkOppositeChefs(AgentsModel agentsModel) {
        assertThat(agentsModel.agents).hasSize(2);
        var agentModels = agentsModel.agents.stream().toList();
        var optimist = agentModels.get(0);
        var pessimist = agentModels.get(1);

        assertThat(optimist.id).isNotNull();
        assertThat(optimist.name).isEqualTo("optimist");
        assertThat(optimist.instruction)
                .startsWith("You are an unwaveringly optimistic and passionate chef who");

        assertThat(pessimist.id).isNotNull();
        assertThat(pessimist.name).isEqualTo("pessimist");
        assertThat(pessimist.instruction)
                .startsWith("You are a jaded, perpetually unimpressed chef who");

        assertThat(optimist.id).isNotEqualTo(pessimist.id);
    }

    @Test
    public void confirmValidEmptyURI() {
        assertThat(URI.create("").toString()).isEmpty();
    }
}
