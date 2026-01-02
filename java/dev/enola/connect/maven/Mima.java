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
package dev.enola.connect.maven;

import static com.google.common.base.Strings.isNullOrEmpty;

import static eu.maveniverse.maven.mima.context.ContextOverrides.ChecksumPolicy.FAIL;
import static eu.maveniverse.maven.mima.context.ContextOverrides.SnapshotUpdatePolicy.NEVER;

import eu.maveniverse.maven.mima.context.Context;
import eu.maveniverse.maven.mima.context.ContextOverrides;
import eu.maveniverse.maven.mima.context.ContextOverrides.ChecksumPolicy;
import eu.maveniverse.maven.mima.context.Runtimes;
import eu.maveniverse.maven.mima.extensions.mmr.MavenModelReader;
import eu.maveniverse.maven.mima.extensions.mmr.ModelRequest;
import eu.maveniverse.maven.mima.extensions.mmr.ModelResponse;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Mima implements AutoCloseable {

    private static final ChecksumPolicy ENOLA_DEFAULT_CHECKSUM_POLICY = FAIL;
    private static final String ENOLA_DEFAULT_CHECKSUM_POLICY_STRING =
            ENOLA_DEFAULT_CHECKSUM_POLICY.name().toLowerCase(Locale.ROOT);

    /**
     * This CENTRAL uses FAIL instead of CHECKSUM_POLICY_WARN like ContextOverrides.CENTRAL does!
     */
    public static final RemoteRepository CENTRAL =
            getRemoteRepository(ContextOverrides.CENTRAL.getUrl());

    public static final RemoteRepository JITPACK = getRemoteRepository("https://jitpack.io/");

    private static final Logger logger = LoggerFactory.getLogger(Mima.class);

    private final Context context;
    private final MavenModelReader mmr;

    public Mima() {
        this(List.of(CENTRAL));
    }

    public Mima(List<RemoteRepository> repos) {
        this(
                ContextOverrides.create()
                        .withUserSettings(false)
                        .addRepositoriesOp(ContextOverrides.AddRepositoriesOp.REPLACE)
                        .repositories(repos)
                        .ignoreArtifactDescriptorRepositories(true)
                        .snapshotUpdatePolicy(NEVER));
        // TODO https://maven.apache.org/resolver/configuration.html configProperties() ?!
    }

    public Mima(ContextOverrides.Builder contextOverridesBuilder) {
        contextOverridesBuilder.checksumPolicy(ENOLA_DEFAULT_CHECKSUM_POLICY);
        // TODO transferListener() to show progress... using a generic Tasks API

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
     * @param gavr a {@link GAVR}
     * @return a {@link ModelResponse}, of which you typically care about the {@link
     *     ModelResponse#getEffectiveModel()}
     */
    public ModelResponse get(GAVR gavr)
            throws ArtifactResolutionException,
                    VersionResolutionException,
                    ArtifactDescriptorException {
        var artifact = gavr.toArtifact();
        var request =
                ModelRequest.builder()
                        .setArtifact(artifact)
                        // TODO https://github.com/maveniverse/mima/issues/166
                        //   .setRepository(gavr.repo())
                        // TODO What is RequestContext really used for?!
                        .setRequestContext(gavr.toString())
                        .build();
        var response = mmr.readModel(request);
        if (response == null) throw new IllegalArgumentException(gavr.toGAV());
        return response;
    }

    // Utilities with access to state of this class

    public DependencyNode collect(GAVR gavr) throws DependencyResolutionException {
        var artifact = gavr.toArtifact();
        Dependency dependency = new Dependency(artifact, "runtime");
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(dependency);

        if (gavr.repo().isEmpty()) collectRequest.setRepositories(context.remoteRepositories());
        else {
            var remoteRepository = getRemoteRepository(gavr.repo());
            // NOT addRepository() but setRepositories(), to clear any existing:
            collectRequest.setRepositories(List.of(remoteRepository));
        }

        DependencyRequest dependencyRequest = new DependencyRequest();
        dependencyRequest.setCollectRequest(collectRequest);

        return context.repositorySystem()
                .resolveDependencies(context.repositorySystemSession(), dependencyRequest)
                .getRoot();
    }

    // Utilities that are purely static "extension" helper methods

    private static RemoteRepository getRemoteRepository(String url) {
        return new RemoteRepository.Builder(url, "default", url)
                .setReleasePolicy(
                        new RepositoryPolicy(
                                true,
                                RepositoryPolicy.UPDATE_POLICY_NEVER,
                                ENOLA_DEFAULT_CHECKSUM_POLICY_STRING))
                .setSnapshotPolicy(
                        new RepositoryPolicy(
                                false,
                                RepositoryPolicy.UPDATE_POLICY_NEVER,
                                ENOLA_DEFAULT_CHECKSUM_POLICY_STRING))
                .build();
    }

    public static String classpath(DependencyNode root) throws DependencyResolutionException {
        PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
        root.accept(nlg);
        return nlg.getClassPath();
    }

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
