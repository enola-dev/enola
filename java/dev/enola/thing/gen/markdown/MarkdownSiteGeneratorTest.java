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

import dev.enola.common.io.iri.namespace.NamespaceConverter;
import dev.enola.common.io.iri.namespace.NamespaceConverterWithRepository;
import dev.enola.common.io.iri.namespace.NamespaceRepositoryEnolaDefaults;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.rdf.RdfReaderConverter;
import dev.enola.rdf.RdfThingConverter;
import dev.enola.thing.*;
import dev.enola.thing.message.JavaThingToProtoThingConverter;
import dev.enola.thing.message.ProtoThingIntoJavaThingBuilderConverter;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.template.TemplateService;
import dev.enola.thing.template.TemplateThingRepository;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MarkdownSiteGeneratorTest {

    ThingProvider NO_THING_PROVIDER = iri -> null;

    NamespaceConverter nc =
            new NamespaceConverterWithRepository(NamespaceRepositoryEnolaDefaults.INSTANCE);

    ResourceProvider rp = new ResourceProviders();

    @Test
    public void picasso() throws Exception {
        var protoThings = load(new ClasspathResource("picasso.ttl"));

        var metadataProvider = new ThingMetadataProvider(NO_THING_PROVIDER, nc);

        Path dir = Files.createTempDirectory("MarkdownSiteGeneratorTest-Picasso");
        var mdDocsGen = new MarkdownSiteGenerator(dir.toUri(), rp, metadataProvider);
        mdDocsGen.generate(protoThings, iri -> false, TemplateService.NONE, true, false);

        check(dir, "example.enola.dev/Picasso.md", "picasso.md");
        check(dir, "example.enola.dev/Dalí.md", "dali.md");
    }

    @Test
    public void templatedGreetingN() throws Exception {
        Path dir = Files.createTempDirectory("MarkdownSiteGeneratorTest-GreetingN");
        generate(dir, "example.org/greetingN.ttl");
        check(dir, "example.org/greeting.md", "greeting.md");
        check(dir, "example.org/greet/_NUMBER.md", "greet-NUMBER.md");
    }

    @Test
    public void templateNameClash() throws IOException {
        Path dir = Files.createTempDirectory("MarkdownSiteGeneratorTest-GreetingN");
        generate(dir, "template-name-clash.ttl");
    }

    private void generate(Path dir, String classpathResource) throws IOException {
        DatatypeRepository dtr = new DatatypeRepositoryBuilder().build();
        var converterP2J = new ProtoThingIntoJavaThingBuilderConverter(dtr);
        var loadedProtoThings = load(new ClasspathResource(classpathResource));
        var repoBuilder = new ThingMemoryRepositoryROBuilder();
        for (var protoThing : loadedProtoThings) {
            var javaThingBuilder = ImmutableThing.builder();
            converterP2J.convertIntoOrThrow(protoThing, javaThingBuilder);
            repoBuilder.store(javaThingBuilder.build());
        }
        var repo = repoBuilder.build();

        var ttr = new TemplateThingRepository(repo);
        var metadataProvider = new ThingMetadataProvider(ttr, nc);

        var converterJ2P = new JavaThingToProtoThingConverter(dtr);
        var templatedThings =
                StreamSupport.stream(ttr.list().spliterator(), false)
                        .map(javaThing -> converterJ2P.convert(javaThing).build())
                        .collect(Collectors.toUnmodifiableSet());

        var mdDocsGen = new MarkdownSiteGenerator(dir.toUri(), rp, metadataProvider);

        mdDocsGen.generate(templatedThings, iri -> ttr.get(iri) != null, ttr, true, false);
    }

    private ImmutableSet<Thing> load(ReadableResource cpr) {
        var rdf4jModel = new RdfReaderConverter().convert(cpr).get();
        var protoThingStream = new RdfThingConverter().convert(rdf4jModel);
        return protoThingStream.map(Thing.Builder::build).collect(ImmutableSet.toImmutableSet());
    }

    private void check(Path dir, String generated, String expected) throws IOException {
        var genMdFileURI = dir.resolve(generated).toUri();
        var generatedMarkdown = rp.getReadableResource(genMdFileURI).charSource().read();
        var trimmedGeneratedMarkdown = trimLineEndWhitespace(generatedMarkdown);

        var expectedMarkdown = new ClasspathResource(expected).charSource().read();
        assertThat(trimmedGeneratedMarkdown).isEqualTo(expectedMarkdown);
    }

    private String trimLineEndWhitespace(String string) {
        return string.replaceAll("(?m) +$", "");
    }
}
