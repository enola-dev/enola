/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.thing;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.iri.namespace.NamespaceConverter;
import dev.enola.common.io.iri.namespace.NamespaceConverterIdentity;

import org.jspecify.annotations.Nullable;
import org.junit.Test;

public class ThingMetadataProviderTest {

    private static final NamespaceConverter NONS = new NamespaceConverterIdentity();

    private static final ThingProvider NO_THING_PROVIDER = iri -> null;

    private String THING_IRI = "http://enola.dev/test";
    private String THING_LABEL = "DaThang!";

    private ThingProvider test =
            new ThingProvider() {

                @Override
                public @Nullable Thing get(String iri) {
                    var builder = ImmutableThing.builder();
                    builder.set(KIRI.SCHEMA.NAME, THING_LABEL);
                    builder.set(KIRI.DC.DESCRIPTION, "...");
                    builder.iri(THING_IRI);
                    return builder.build();
                }
            };

    private ThingProvider error =
            new ThingProvider() {

                @Override
                public Thing get(String iri) {
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
}
