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
package dev.enola.rdf;

import static dev.enola.rdf.ModelSubject.assertThat;
import static dev.enola.rdf.ResourceSubject.assertThat;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.Resource;

import org.eclipse.rdf4j.model.Model;
import org.junit.Test;

import java.io.IOException;

public class RdfReaderWriterTest {

    private static final Model PICASSO_MODEL = new LearnRdf4jTest().picasso2();

    private static final ClasspathResource PICASSO_TURTLE_RESOURCE =
            new ClasspathResource("picasso.ttl", RdfMediaType.TURTLE);

    private static final ClasspathResource PICASSO_JSONLD_RESOURCE =
            new ClasspathResource("picasso.jsonld", RdfMediaType.JSON_LD);

    @Test
    // 🎨 as 🐢 https://www.w3.org/TR/turtle
    public void writeTurtle() throws ConversionException, IOException {
        Resource actual = new MemoryResource(RdfMediaType.TURTLE);
        new RdfWriterConverter().convertInto(PICASSO_MODEL, actual);

        var expected = PICASSO_TURTLE_RESOURCE;
        assertThat(actual).containsCharsOf(expected);
    }

    @Test
    public void readTurtle() throws ConversionException {
        var model = new RdfReaderConverter().convert(PICASSO_TURTLE_RESOURCE);
        assertThat(model).isEqualTo(PICASSO_MODEL);
    }

    @Test
    // 🎨 as https://json-ld.org
    public void writeJsonLD() throws ConversionException, IOException {
        var actual = new MemoryResource(RdfMediaType.JSON_LD);
        new RdfWriterConverter().convertInto(PICASSO_MODEL, actual);

        var expected = PICASSO_JSONLD_RESOURCE;
        assertThat(actual).hasJSONEqualTo(expected);
    }

    @Test
    public void readJsonLD() throws ConversionException {
        var model = new RdfReaderConverter().convert(PICASSO_JSONLD_RESOURCE);
        assertThat(model).isEqualTo(PICASSO_MODEL);
    }
}
