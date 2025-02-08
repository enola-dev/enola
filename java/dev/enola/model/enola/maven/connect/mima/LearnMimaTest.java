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
package dev.enola.model.enola.maven.connect.mima;

import static com.google.common.truth.Truth.assertThat;

import static eu.maveniverse.maven.mima.context.ContextOverrides.ChecksumPolicy.FAIL;
import static eu.maveniverse.maven.mima.context.ContextOverrides.SnapshotUpdatePolicy.NEVER;

import eu.maveniverse.maven.mima.context.Context;
import eu.maveniverse.maven.mima.context.ContextOverrides;
import eu.maveniverse.maven.mima.context.Runtimes;
import eu.maveniverse.maven.mima.extensions.mmr.MavenModelReader;
import eu.maveniverse.maven.mima.extensions.mmr.ModelRequest;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LearnMimaTest {

    // TODO Move some stuff that's initially here into class Mima afterwards...

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void mima() throws RepositoryException {
        var gav = "ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0";

        var overrides =
                ContextOverrides.create()
                        // TODO repositories() - ContextOverrides.AddRepositoriesOp?
                        // TODO transferListener() to log progress?
                        .checksumPolicy(FAIL)
                        .snapshotUpdatePolicy(NEVER)
                        .build();
        // TODO https://maven.apache.org/resolver/configuration.html configProperties() ?!

        var runtime = Runtimes.INSTANCE.getRuntime();
        logger.info("Mima Maven Version: {}", runtime.mavenVersion());

        try (Context context = runtime.create(overrides)) {
            var mmr = new MavenModelReader(context);
            var request =
                    ModelRequest.builder()
                            .setArtifact(new DefaultArtifact(gav))
                            // TODO What is RequestContext used for?!
                            .setRequestContext("LearnMimaTest-demo")
                            .build();
            var response = mmr.readModel(request);
            var model = response.getEffectiveModel();
            assertThat(model).isNotNull();
        }
    }
}
