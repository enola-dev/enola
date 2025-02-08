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

import static com.google.common.base.Strings.isNullOrEmpty;

import static eu.maveniverse.maven.mima.context.ContextOverrides.ChecksumPolicy.FAIL;
import static eu.maveniverse.maven.mima.context.ContextOverrides.SnapshotUpdatePolicy.NEVER;

import eu.maveniverse.maven.mima.context.Context;
import eu.maveniverse.maven.mima.context.ContextOverrides;
import eu.maveniverse.maven.mima.context.Runtimes;
import eu.maveniverse.maven.mima.extensions.mmr.MavenModelReader;
import eu.maveniverse.maven.mima.extensions.mmr.ModelRequest;
import eu.maveniverse.maven.mima.extensions.mmr.ModelResponse;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Mima implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(Mima.class);

    private final Context context;
    private final MavenModelReader mmr;

    public Mima() {
        this(
                ContextOverrides.create()
                        // TODO repositories() - ContextOverrides.AddRepositoriesOp?
                        // TODO transferListener() to log progress?
                        .snapshotUpdatePolicy(NEVER));
        // TODO https://maven.apache.org/resolver/configuration.html configProperties() ?!
    }

    public Mima(ContextOverrides.Builder contextOverridesBuilder) {
        contextOverridesBuilder.checksumPolicy(FAIL);

        var env = "TEST_TMPDIR"; // see https://bazel.build/reference/test-encyclopedia
        var tmp = System.getenv(env);
        if (tmp != null) {
            Path m2repo = Paths.get(tmp, "maven/repo");
            contextOverridesBuilder.withLocalRepositoryOverride(m2repo);
        }

        var runtime = Runtimes.INSTANCE.getRuntime();
        logger.info("Mima Maven Version: {}", runtime.mavenVersion());

        this.context = runtime.create(contextOverridesBuilder.build());
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
        if (response == null) throw new IllegalArgumentException(gav);
        return response;
    }

    // Utilities

    public static Optional<URI> origin(ModelResponse modelResponse) {
        var model = modelResponse.getRawModel();
        var artifactDescriptorResult = modelResponse.toArtifactDescriptorResult(model);
        var repository = artifactDescriptorResult.getRepository();
        if (repository instanceof RemoteRepository remoteRepository) {
            return Optional.of(URI.create(remoteRepository.getUrl()));
        }
        return Optional.empty();
    }

    public static String xml(Model model) {
        try (var baos = new ByteArrayOutputStream()) {
            String encoding = model.getModelEncoding();
            if (isNullOrEmpty(encoding)) encoding = "UTF-8";
            try (Writer out = new OutputStreamWriter(baos, encoding)) {
                new MavenXpp3Writer().write(out, model);
            }
            return baos.toString(encoding);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
