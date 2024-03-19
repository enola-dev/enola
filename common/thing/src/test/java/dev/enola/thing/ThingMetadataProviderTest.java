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

import dev.enola.thing.proto.Thing;

import org.junit.Test;

import java.io.IOException;

public class ThingMetadataProviderTest {

    private ThingProvider empty =
            new ThingProvider() {

                @Override
                public Thing getThing(String iri) throws IOException {
                    return Thing.getDefaultInstance();
                }
            };

    private String THING_IRI = "http://enola.dev/test";
    private String THING_LABEL = "DaThang!";

    private ThingProvider test =
            new ThingProvider() {

                @Override
                public Thing getThing(String iri) throws IOException {
                    var builder = Thing.newBuilder();
                    ThingExt.setString(builder, KIRI.SCHEMA.ID, THING_IRI);
                    ThingExt.setString(builder, KIRI.SCHEMA.NAME, THING_LABEL);
                    return builder.build();
                }
            };

    private ThingProvider error =
            new ThingProvider() {

                @Override
                public Thing getThing(String iri) throws IOException {
                    throw new IOException();
                }
            };

    @Test
    public void id() {
        var iri = "https://server/thing";
        assertThat(new ThingMetadataProvider(empty).getID(iri)).isEqualTo(iri);

        assertThat(new ThingMetadataProvider(test).getID(iri)).isEqualTo(THING_IRI);
    }

    @Test
    public void label() {
        assertThat(new ThingMetadataProvider(empty).getLabel(THING_IRI)).isEqualTo("test");

        assertThat(new ThingMetadataProvider(test).getLabel(THING_IRI)).isEqualTo(THING_LABEL);
    }
}
