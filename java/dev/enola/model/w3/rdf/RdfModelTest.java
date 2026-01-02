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
package dev.enola.model.w3.rdf;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.thing.KIRI;

import org.junit.Test;

import java.util.stream.StreamSupport;

public class RdfModelTest {

    @Test
    public void propertyBuilderUnchained() {
        var builder = Property.builder();
        builder.iri("https://example.org/property1");
        builder.label("Property #1");
        Property property1 = builder.build();
        check(property1);
    }

    @Test
    public void propertyBuilderChainWithoutIRI() {
        var builder = Property.builder().label("Property #1");
        builder.iri("https://example.org/property1");
        Property property1 = builder.build();
        check(property1);
    }

    @Test
    public void propertyBuilderChainLastIRI() {
        var builder = Property.builder().label("Property #1").iri("https://example.org/property1");
        Property property1 = builder.build();
        check(property1);
    }

    @Test
    public void propertyBuilderChainFirstIRI() {
        var builder = Property.builder().iri("https://example.org/property1").label("Property #1");
        Property property1 = builder.build();
        check(property1);
    }

    private void check(Property property) {
        assertThat(property.label()).isEqualTo("Property #1");
        assertThat(iterableToString(property.typesIRIs())).containsExactly(KIRI.RDFS.CLASS);
    }

    //
    private static Iterable<String> iterableToString(Iterable<?> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).map(Object::toString).toList();
    }

    @Test
    public void copyProperty() {
        Property property1 =
                Property.builder()
                        .iri("https://example.org/property1")
                        .label("Property #1")
                        .build();
        Property property2 = property1.copy().label("CHANGED").build();
        assertThat(property2.label()).isEqualTo("CHANGED");
        assertThat(property2.iri()).isEqualTo("https://example.org/property1");
    }
}
