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
package dev.enola.model.enola.mediatype;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.model.enola.mediatype.TikaMediaTypesThingConverter.IRI;

import dev.enola.common.context.TLC;
import dev.enola.thing.impl.MutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;
import dev.enola.thing.repo.ThingRepositoryStore;

import org.junit.Test;

import java.io.IOException;

public class TikaMediaTypesThingConverterTest {

    @Test
    public void proxy() {
        ProxyTBF proxy = new ProxyTBF(MutableThing.FACTORY);
        MediaType.Builder builder = proxy.create(MediaType.Builder.class, MediaType.class);
        builder.label("yo").iri("http://example.com");
        MediaType mediaType = builder.build();
        assertThat(mediaType.label()).isEqualTo("yo");
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void convert() throws IOException {
        try (var ctx = TLC.open().push(TBF.class, new ProxyTBF(MutableThing.FACTORY))) {
            ThingRepositoryStore builder = new ThingMemoryRepositoryROBuilder();
            new TikaMediaTypesThingConverter().convertInto(IRI, builder);
        }
    }
}
