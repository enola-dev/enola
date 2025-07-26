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

import static dev.enola.common.collect.MoreIterables.toCollection;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.ObjectReaderChain;
import dev.enola.common.io.object.jackson.JsonObjectReaderWriter;
import dev.enola.common.io.object.jackson.YamlObjectReaderWriter;
import dev.enola.common.io.resource.ResourceProvider;

import java.io.IOException;
import java.net.URI;

public class AgentsModelLoader {

    // TODO Support chefs-opposites-map.agent.yaml in addition to chefs-opposites-stream.agent.yaml
    //   But how to determine which? Use *.agents.yaml vs. *agent.yaml filename? Or $schema ?
    //   See AgentsModelLoaderTest.oppositeChefsMap()

    // TODO Validate against agent.schema.yaml while loading

    // TODO Later support loading from .txtpb, in addition to .yaml and .json

    private final ObjectReader objectReader =
            new ObjectReaderChain(new YamlObjectReaderWriter(), new JsonObjectReaderWriter());

    private final ResourceProvider resourceProvider;

    public AgentsModelLoader(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    public AgentsModel load(URI uri) throws IOException {
        var agentsModel = new AgentsModel();
        var resource = resourceProvider.getNonNull(uri);
        var agents = objectReader.readStream(resource, AgentsModel.Agent.class);
        if (Iterables.isEmpty(agents)) throw new IOException("No agent/s in: " + uri);

        var fileName = URIs.getFilenameWithoutExtension(uri, ".agent.yaml", ".agent.json");
        for (var agent : agents) {
            // TODO Handle multiple agents as #1, #2 etc. instead of same name & id for all...
            if (agent.id == null || agent.id.toString().isEmpty()) agent.id = uri;
            if (Strings.isNullOrEmpty(agent.name)) agent.name = fileName;
        }
        agentsModel.agents.addAll(toCollection(agents));
        return agentsModel;
    }
}
