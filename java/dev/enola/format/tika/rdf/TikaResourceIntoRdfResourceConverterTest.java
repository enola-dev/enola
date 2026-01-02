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
package dev.enola.format.tika.rdf;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.testlib.EnolaTestTLCRules;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.rdf.io.RdfMediaTypes;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;
import java.net.URI;

public class TikaResourceIntoRdfResourceConverterTest {

    @Rule public final SingletonRule r = $(MediaTypeProviders.set(new MediaTypeProviders()));

    @Rule public final TestRule tlcRule = EnolaTestTLCRules.TBF;

    @Test
    public void html() throws IOException {
        var rp = new ClasspathResource.Provider();
        var m = new MemoryResource(RdfMediaTypes.TURTLE);
        var c = new TikaResourceIntoRdfResourceConverter(rp);
        var r = c.convertInto(rp.getResource(URI.create("classpath:/test.html")), m);
        assertThat(r).isTrue();
    }
}
