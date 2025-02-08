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

import static eu.maveniverse.maven.mima.context.ContextOverrides.ChecksumPolicy.FAIL;
import static eu.maveniverse.maven.mima.context.ContextOverrides.SnapshotUpdatePolicy.NEVER;

import eu.maveniverse.maven.mima.context.Context;
import eu.maveniverse.maven.mima.context.ContextOverrides;
import eu.maveniverse.maven.mima.context.Runtimes;
import eu.maveniverse.maven.mima.extensions.mmr.MavenModelReader;
import eu.maveniverse.maven.mima.extensions.mmr.ModelRequest;
import eu.maveniverse.maven.mima.extensions.mmr.ModelResponse;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mima implements AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Context context;
    private final MavenModelReader mmr;

    public Mima() {
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

        this.context = runtime.create(overrides);
        this.mmr = new MavenModelReader(context);
    }

    @Override
    public void close() {
        context.close();
    }

    /**
     * Fetch a Maven Model from (remote) repositories, given a GAV.
     *
     * @param gav a "Gradle-style" GAV in the
     *     <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version> format, like e.g.
     *     "ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0"
     * @return a {@link ModelResponse}, of which you typically care about the {@link
     *     ModelResponse#getEffectiveModel()}
     */
    public ModelResponse get(String gav)
            throws ArtifactResolutionException,
                    VersionResolutionException,
                    ArtifactDescriptorException {
        var request =
                ModelRequest.builder()
                        .setArtifact(new DefaultArtifact(gav))
                        // TODO What is RequestContext really used for?!
                        .setRequestContext(gav)
                        .build();
        var response = mmr.readModel(request);
        return response;
    }
}
