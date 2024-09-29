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
package dev.enola.format.xml;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.xml.XmlResourceParser;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class XMLToMultimapHandlerTest {

    private final ResourceProvider rp = new ClasspathResource.Provider();
    private final XmlResourceParser xmlParser = new XmlResourceParser();

    @Test
    public void greeting1nesteds() throws IOException {
        // TODO "classpath:/test.html.xml"
        var handler = new XMLToMultimapHandler("https://example.org/");
        var from = rp.getReadableResource(URI.create("classpath:/greeting1-nesteds.xml"));
        xmlParser.convertInto(from, handler);
        throw new IllegalStateException(handler.getRoot().toString());
    }
}
