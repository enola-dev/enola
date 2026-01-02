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
package dev.enola.common.xml;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.io.mediatype.MediaTypes.normalizedNoParamsEquals;

import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;

import org.junit.Rule;
import org.junit.Test;

import java.net.URI;

public class XmlMediaTypeTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new XmlMediaType()));

    @Test
    public void xmlMediaType() {
        var rp = new ClasspathResource.Provider();
        var resource = rp.get("classpath:/greeting1-attribute.xml");
        var mediaType = resource.mediaType();
        assertThat(normalizedNoParamsEquals(resource.mediaType(), MediaType.XML_UTF_8)).isTrue();
    }

    @Test
    public void testStrangeBug() {
        var rp = new ClasspathResource.Provider();
        var ok = rp.getReadableResource(URI.create("classpath:/greeting1-nested.xml"));
        assertThat(normalizedNoParamsEquals(ok.mediaType(), MediaType.XML_UTF_8)).isTrue();

        // This XML used to start with <!-- comment and without <?xml and was HTML instead of XML
        var nok = rp.getReadableResource(URI.create("classpath:/greeting1-attribute.xml"));
        assertThat(normalizedNoParamsEquals(nok.mediaType(), MediaType.XML_UTF_8)).isTrue();
    }
}
