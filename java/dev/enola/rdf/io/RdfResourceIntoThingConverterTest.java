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
package dev.enola.rdf.io;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.resource.*;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingsBuilder;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class RdfResourceIntoThingConverterTest {

    DatatypeRepository datatypeRepository = new DatatypeRepositoryBuilder().build();
    ResourceProvider resourceProvider =
            new ResourceProviders(new ClasspathResource.Provider(), new EmptyResource.Provider());
    RdfResourceIntoThingConverter c =
            new RdfResourceIntoThingConverter<Thing>(resourceProvider, datatypeRepository);

    @Test
    public void picasso() throws IOException {
        var thing = convert(new ClasspathResource("picasso.ttl").uri()).iterator().next().build();
        assertThat(thing.iri()).isEqualTo("http://example.enola.dev/Dalí");
    }

    @Test
    public void emptyYAML() throws IOException {
        assertThat(convert(new ClasspathResource("empty.yaml").uri())).isEmpty();
    }

    @Test
    public void empty() throws IOException {
        assertThat(convert(EmptyResource.EMPTY_URI)).isEmpty();
    }

    private Iterable<Thing.Builder<?>> convert(URI uri) throws IOException {
        var thingsBuilder = new ThingsBuilder();
        c.convertInto(uri, thingsBuilder);
        return thingsBuilder.builders();
    }
}
