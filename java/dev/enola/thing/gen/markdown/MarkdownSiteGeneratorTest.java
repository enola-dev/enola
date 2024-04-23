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
package dev.enola.thing.gen.markdown;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableSet;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.rdf.RdfReaderConverter;
import dev.enola.rdf.RdfThingConverter;
import dev.enola.thing.proto.Thing;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MarkdownSiteGeneratorTest {

    ResourceProvider rp = new ResourceProviders();

    @Test
    public void picasso() throws Exception {
        var cpr = new ClasspathResource("picasso.ttl");
        var rdf4jModel = new RdfReaderConverter().convert(cpr).get();
        var protoThingStream = new RdfThingConverter().convert(rdf4jModel);
        var protoThings =
                protoThingStream.map(Thing.Builder::build).collect(ImmutableSet.toImmutableSet());

        Path dir = Files.createTempDirectory("MarkdownSiteGeneratorTest");
        var mdDocsGen = new MarkdownSiteGenerator(dir.toUri(), rp);
        mdDocsGen.generate(protoThings, iri -> false);

        check(dir, "example.enola.dev/Picasso.md", "picasso.md");
        check(dir, "example.enola.dev/Dalí.md", "dali.md");
    }

    private void check(Path dir, String generated, String expected) throws IOException {
        var genPabloMdFileURI = dir.resolve(generated).toUri();
        var generatedMarkdown = rp.getReadableResource(genPabloMdFileURI).charSource().read();
        var trimmedGeneratedMarkdown = trimLineEndWhitespace(generatedMarkdown);

        var expectedMarkdown = new ClasspathResource(expected).charSource().read();
        assertThat(trimmedGeneratedMarkdown).isEqualTo(expectedMarkdown);
    }

    private String trimLineEndWhitespace(String string) {
        return string.replaceAll("(?m) +$", "");
    }
}
