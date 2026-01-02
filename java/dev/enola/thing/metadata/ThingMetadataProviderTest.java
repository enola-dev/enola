/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.metadata;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.testlib.EnolaTestTLCRules;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.context.testlib.TestTLCRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.data.iri.NamespaceConverter;
import dev.enola.data.iri.NamespaceConverterIdentity;
import dev.enola.rdf.io.RdfLoader;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.repo.ThingMemoryRepositoryRW;
import dev.enola.thing.repo.ThingProvider;

import org.jspecify.annotations.Nullable;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class ThingMetadataProviderTest {

    @Rule public SingletonRule r = $(MediaTypeProviders.set(new RdfMediaTypes()));

    @Rule public TestTLCRule rlcRule = EnolaTestTLCRules.BASIC;

    private static final NamespaceConverter NONS = new NamespaceConverterIdentity();

    private static final ThingProvider NO_THING_PROVIDER = iri -> null;

    private final String THING_IRI = "http://enola.dev/test";
    private final String THING_LABEL = "DaThang!";

    private final ThingProvider test =
            iri -> {
                var builder = ImmutableThing.builder();
                builder.set(KIRI.SCHEMA.NAME, THING_LABEL);
                builder.set(KIRI.DC.DESCRIPTION, "...");
                builder.iri(THING_IRI);
                return builder.build();
            };

    private final ThingProvider error =
            new ThingProvider() {

                @Override
                public @Nullable Thing get(String iri) {
                    throw new IllegalStateException();
                }
            };

    @Test
    public void label() {
        assertThat(
                        new ThingMetadataProvider(NO_THING_PROVIDER, NONS)
                                .get("http://enola.dev/")
                                .label())
                .isEqualTo("enola.dev");

        assertThat(
                        new ThingMetadataProvider(NO_THING_PROVIDER, NONS)
                                .get("http://enola.dev")
                                .label())
                .isEqualTo("enola.dev");

        assertThat(new ThingMetadataProvider(NO_THING_PROVIDER, NONS).get(THING_IRI).label())
                .isEqualTo("test");

        assertThat(new ThingMetadataProvider(NO_THING_PROVIDER, NONS).get(THING_IRI + "/").label())
                .isEqualTo("test");

        assertThat(new ThingMetadataProvider(test, NONS).get(THING_IRI).label())
                .isEqualTo(THING_LABEL);
    }

    @Test
    public void labelViaAlternativeLabelProperty() throws IOException {
        var uri = java.net.URI.create("classpath:/metadata-label-property.ttl");
        var repo = new ThingMemoryRepositoryRW();
        var things = new RdfLoader().load(uri, repo);
        var metadataProvider = new ThingMetadataProvider(repo, NONS);
        var metadata = metadataProvider.get("https://example.org/test-metadata-label-property");
        assertThat(metadata.label()).isEqualTo("LABEL");
    }

    @Test
    public void description() {
        assertThat(new ThingMetadataProvider(test, NONS).get(THING_IRI).descriptionHTML())
                .isEqualTo("...");
    }

    @Test
    public void error() {
        var meta = new ThingMetadataProvider(error, NONS).get(THING_IRI);
        assertThat(meta.label()).isNotEmpty();
        assertThat(meta.descriptionHTML()).isEmpty();
        assertThat(meta.imageHTML()).isEmpty();
    }

    @Test
    public void labelOfFileDirectory() {
        var meta = new ThingMetadataProvider(NO_THING_PROVIDER, NONS).get("file:///tmp/");
        assertThat(meta.label()).isEqualTo("tmp");
        assertThat(meta.descriptionHTML()).isEmpty();
        assertThat(meta.imageHTML()).isEmpty();
    }
}
