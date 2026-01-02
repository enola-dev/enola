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
package dev.enola.rdf.io;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.io.testlib.ResourceSubject.assertThat;
import static dev.enola.rdf.io.ModelSubject.assertThat;
import static dev.enola.rdf.io.RdfMediaTypeYamlLd.YAML_LD;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.*;

import org.eclipse.rdf4j.model.Model;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;

public class RdfReaderWriterTest {

    public static final @ClassRule SingletonRule r =
            $(
                    MediaTypeProviders.set(
                            new RdfMediaTypes(), new RdfMediaTypeYamlLd(), new YamlMediaType()));

    private static final Model PICASSO_MODEL = new LearnRdf4jTest().picassoAndDali2();

    private static final ResourceProvider rp = new ClasspathResource.Provider();

    private static final Resource PICASSO_TURTLE_RESOURCE = rp.get("classpath:/picasso.ttl");

    private static final Resource PICASSO_TURTLE_WRITTEN_RESOURCE =
            rp.get("classpath:/picasso.written.ttl");

    private static final Resource PICASSO_JSONLD_RESOURCE = rp.get("classpath:/picasso.jsonld");

    private static final Resource PICASSO_JSON_RESOURCE =
            rp.get("classpath:/picasso.json?context=classpath:/picasso-context.jsonld");

    private static final Resource PICASSO_YAML_RESOURCE =
            rp.get("classpath:/picasso.yaml?context=classpath:/picasso-context.jsonld");

    private static final Resource PICASSO_YAMLLD_RESOURCE = rp.get("classpath:/picasso.yamlld");

    @Test
    // 🎨 as 🐢 https://www.w3.org/TR/turtle
    public void writeTurtle() throws ConversionException, IOException {
        Resource actual =
                new MemoryResource(PICASSO_TURTLE_WRITTEN_RESOURCE.uri(), RdfMediaTypes.TURTLE);
        new RdfWriterConverter().convertInto(PICASSO_MODEL, actual);

        var expected = PICASSO_TURTLE_WRITTEN_RESOURCE;
        assertThat(actual).containsCharsOf(expected);
    }

    @Test
    public void readTurtle() throws ConversionException {
        var model = new RdfReaderConverter(rp).convert(PICASSO_TURTLE_RESOURCE).get();
        assertThat(model).isEqualTo(PICASSO_MODEL);
    }

    @Test
    // 🎨 as https://json-ld.org
    public void writeJsonLD() throws ConversionException, IOException {
        var actual = new MemoryResource(RdfMediaTypes.JSON_LD);
        new RdfWriterConverter().convertInto(PICASSO_MODEL, actual);

        var expected = PICASSO_JSONLD_RESOURCE;
        assertThat(actual).hasJSONEqualTo(expected);
    }

    @Test
    public void readJsonLD() throws ConversionException {
        var model = new RdfReaderConverter(rp).convert(PICASSO_JSONLD_RESOURCE).get();
        assertThat(model).isEqualTo(PICASSO_MODEL);
    }

    @Test
    public void readJsonWithContext() throws ConversionException {
        assertThat(PICASSO_JSON_RESOURCE.uri().getQuery()).isNotEmpty();
        var model = new RdfReaderConverter(rp).convert(PICASSO_JSON_RESOURCE).get();
        assertThat(model).isEqualTo(PICASSO_MODEL);
    }

    @Test
    public void readYamlWithContext() throws ConversionException {
        var mediaType = PICASSO_YAML_RESOURCE.mediaType();
        assertThat(mediaType).isEqualTo(YamlMediaType.YAML_UTF_8);
        var model = new RdfReaderConverter(rp).convert(PICASSO_YAML_RESOURCE).get();
        assertThat(model).isEqualTo(PICASSO_MODEL);
    }

    @Test
    public void readYamlLD() throws ConversionException {
        var mediaType = PICASSO_YAMLLD_RESOURCE.mediaType();
        assertThat(mediaType).isEqualTo(RdfMediaTypeYamlLd.YAML_LD);
        assertThat(MediaTypes.normalizedNoParamsEquals(mediaType, YAML_LD)).isTrue();

        var model = new RdfReaderConverter(rp).convert(PICASSO_YAMLLD_RESOURCE).get();
        assertThat(model).isEqualTo(PICASSO_MODEL);
    }
}
