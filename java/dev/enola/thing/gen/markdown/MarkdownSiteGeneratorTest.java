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
package dev.enola.thing.gen.markdown;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.io.testlib.ResourceSubject.assertThat;
import static dev.enola.thing.template.Templates.Format.Mustache;

import com.google.common.collect.ImmutableSet;

import dev.enola.common.context.testlib.EnolaTestTLCRules;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MarkdownMediaTypes;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.StandardMediaTypes;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.data.iri.NamespaceConverter;
import dev.enola.data.iri.namespace.repo.NamespaceConverterWithRepository;
import dev.enola.data.iri.namespace.repo.NamespaceRepositoryEnolaDefaults;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.model.Datatypes;
import dev.enola.model.enola.files.FileThingConverter;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.rdf.io.RdfReaderConverter;
import dev.enola.rdf.proto.RdfProtoThingsConverter;
import dev.enola.thing.gen.gexf.GexfMediaType;
import dev.enola.thing.gen.graphviz.GraphvizMediaType;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.message.JavaThingToProtoThingConverter;
import dev.enola.thing.message.ProtoThingIntoJavaThingBuilderConverter;
import dev.enola.thing.message.ProtoThingMetadataProvider;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;
import dev.enola.thing.repo.ThingProvider;
import dev.enola.thing.template.TemplateService;
import dev.enola.thing.template.TemplateThingRepository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MarkdownSiteGeneratorTest {

    @Rule
    public SingletonRule r =
            $(
                    MediaTypeProviders.set(
                            new RdfMediaTypes(),
                            new GexfMediaType(),
                            new GraphvizMediaType(),
                            new MarkdownMediaTypes(),
                            new StandardMediaTypes()));

    @Rule public final TestRule tlcRule = EnolaTestTLCRules.BASIC;

    ThingProvider NO_THING_PROVIDER = iri -> null;

    DatatypeRepository dtr = Datatypes.DTR;

    NamespaceConverter nc =
            new NamespaceConverterWithRepository(NamespaceRepositoryEnolaDefaults.INSTANCE);

    ThingMetadataProvider thingMetadataProvider = new ThingMetadataProvider(NO_THING_PROVIDER, nc);
    ProtoThingMetadataProvider protoThingMetadataProvider =
            new ProtoThingMetadataProvider(thingMetadataProvider);

    ResourceProvider rp = new ResourceProviders();

    @Test
    public void picasso() throws Exception {
        var protoThings = load(new ClasspathResource("picasso.ttl"));

        Path dir = Files.createTempDirectory("MarkdownSiteGeneratorTest-Picasso");
        var mdDocsGen =
                new MarkdownSiteGenerator(
                        dir.toUri(),
                        rp,
                        thingMetadataProvider,
                        protoThingMetadataProvider,
                        dtr,
                        Mustache);
        mdDocsGen.generate(
                protoThings, iri -> null, iri -> false, TemplateService.NONE, true, false);

        check(dir, "example.enola.dev/Picasso.md", "picasso.md");
        check(dir, "example.enola.dev/Dalí.md", "dali.md");

        // Cover MarkdownIndexGenerator
        check(dir, MarkdownSiteGenerator.TYPES_MD, "picasso.index.md");
        check(dir, MarkdownSiteGenerator.HIERARCHY_MD, "picasso.hierarchy.md");
    }

    @Test
    public void directory() throws Exception {
        var c = new FileThingConverter();
        var b = new ThingMemoryRepositoryROBuilder();
        c.convertIntoOrThrow(URI.create("file:/tmp/"), b);
        var javaThing = b.list().iterator().next();
        var protoThing = new JavaThingToProtoThingConverter(dtr).convert(javaThing).build();
        var protoThings = Set.of(protoThing);

        Path dir = Files.createTempDirectory("MarkdownSiteGeneratorTest-Directory");
        var mdDocsGen =
                new MarkdownSiteGenerator(
                        dir.toUri(),
                        rp,
                        thingMetadataProvider,
                        protoThingMetadataProvider,
                        dtr,
                        Mustache);
        mdDocsGen.generate(
                protoThings, iri -> null, iri -> false, TemplateService.NONE, true, false);
    }

    @Test // ~same (as integration instead of unit test) also in
    // EnolaCLITest#docGenTemplatedGreetingN()
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

    @Test
    public void listOfList() throws IOException {
        Path dir = Files.createTempDirectory("MarkdownSiteGeneratorTest-listOfList");
        generate(dir, "list-of-list.ttl");
        check(dir, "example.org/list-of-list.md", "list-of-list.md");
    }

    private void generate(Path dir, String classpathResource) throws IOException {
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
        var metadataProvider = new ProtoThingMetadataProvider(new ThingMetadataProvider(ttr, nc));

        var converterJ2P = new JavaThingToProtoThingConverter(dtr);
        var templatedThings =
                StreamSupport.stream(ttr.list().spliterator(), false)
                        .map(javaThing -> converterJ2P.convert(javaThing).build())
                        .collect(Collectors.toUnmodifiableSet());

        var mdDocsGen =
                new MarkdownSiteGenerator(
                        dir.toUri(),
                        rp,
                        thingMetadataProvider,
                        protoThingMetadataProvider,
                        dtr,
                        Mustache);

        mdDocsGen.generate(
                templatedThings, iri -> null, iri -> ttr.get(iri) != null, ttr, true, false);
    }

    private ImmutableSet<Thing> load(ReadableResource cpr) {
        var rdf4jModel = new RdfReaderConverter(iri -> null).convert(cpr).get();
        var protoThingStream = new RdfProtoThingsConverter().convert(rdf4jModel);
        return protoThingStream.map(Thing.Builder::build).collect(ImmutableSet.toImmutableSet());
    }

    private void check(Path dir, String generated, String expected) throws IOException {
        var genMdFileURI = dir.resolve(generated).toUri();
        var generatedMarkdownResource = rp.getReadableResource(genMdFileURI);
        var expectedMarkdownResource = new ClasspathResource(expected);
        assertThat(generatedMarkdownResource).containsCharsOfIgnoreEOL(expectedMarkdownResource);
    }
}
