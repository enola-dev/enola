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

import dev.enola.common.io.iri.NamespaceConverter;
import dev.enola.common.io.iri.NamespaceConverterIdentity;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.thing.message.ThingAdapter;

import org.junit.Test;

public class ThingMetadataProviderTest {

    private static final NamespaceConverter NONS = new NamespaceConverterIdentity();

    private static final DatatypeRepository EMPTY_DTR = new DatatypeRepositoryBuilder().build();

    private ThingProvider empty =
            new ThingProvider() {

                @Override
                public Thing get(String iri) {
                    return new ThingAdapter(
                            dev.enola.thing.proto.Thing.getDefaultInstance(), EMPTY_DTR);
                }
            };

    private String THING_IRI = "http://enola.dev/test";
    private String THING_LABEL = "DaThang!";

    private ThingProvider test =
            new ThingProvider() {

                @Override
                public Thing get(String iri) {
                    var builder = ImmutableThing.builder();
                    builder.set(KIRI.SCHEMA.ID, THING_IRI);
                    builder.set(KIRI.SCHEMA.NAME, THING_LABEL);
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
    public void id() {
        var iri = "https://server/thing";
        assertThat(new ThingMetadataProvider(empty, NONS).getID(iri)).isEqualTo(iri);

        assertThat(new ThingMetadataProvider(test, NONS).getID(iri)).isEqualTo(THING_IRI);
    }

    @Test
    public void label() {
        assertThat(new ThingMetadataProvider(empty, NONS).getLabel(THING_IRI)).isEqualTo("test");

        assertThat(new ThingMetadataProvider(test, NONS).getLabel(THING_IRI))
                .isEqualTo(THING_LABEL);
    }

    @Test
    public void error() {
        assertThat(new ThingMetadataProvider(error, NONS).getID(THING_IRI)).isNotEmpty();
        assertThat(new ThingMetadataProvider(error, NONS).getLabel(THING_IRI)).isNotEmpty();
        assertThat(new ThingMetadataProvider(error, NONS).getDescriptionHTML(THING_IRI)).isEmpty();
        assertThat(new ThingMetadataProvider(error, NONS).getImageHTML(THING_IRI)).isEmpty();
    }
}
