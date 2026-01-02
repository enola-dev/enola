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
package dev.enola.format.tika;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.google.common.net.MediaType;

import dev.enola.common.StringBuilderWriter;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.locale.LocaleSupplier;
import dev.enola.common.locale.LocaleSupplierTLC;
import dev.enola.data.iri.IRI;
import dev.enola.data.iri.NamespaceConverter;
import dev.enola.data.iri.namespace.repo.NamespaceConverterWithRepository;
import dev.enola.data.iri.namespace.repo.NamespaceRepositoryEnolaDefaults;
import dev.enola.thing.Link;
import dev.enola.thing.Thing;
import dev.enola.thing.io.UriIntoThingConverter;
import dev.enola.thing.repo.ThingRepositoryStore;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.*;

public class TikaThingConverter implements UriIntoThingConverter {

    private static final Set<MediaType> IGNORED =
            ImmutableSet.of(
                    MediaType.XML_UTF_8.withoutParameters(),
                    MediaType.parse("application/xml"),
                    MediaType.parse("text/turtle"),
                    MediaType.parse("application/x-turtle"));

    private static final AutoDetectParser parser = new AutoDetectParser();

    private final ResourceProvider rp;
    private final NamespaceConverter namespaceConverter;
    private final LocaleSupplier localeSupplier = LocaleSupplierTLC.JVM_DEFAULT;

    public TikaThingConverter(ResourceProvider resourceProvider) {
        this.rp = resourceProvider;
        this.namespaceConverter =
                new NamespaceConverterWithRepository(NamespaceRepositoryEnolaDefaults.INSTANCE);
    }

    @Override
    public boolean convertInto(URI from, ThingRepositoryStore store)
            throws ConversionException, IOException {
        var resource = rp.getReadableResource(from);
        if (resource == null) return false;
        return convertInto(resource, store);
    }

    private boolean convertInto(ReadableResource resource, ThingRepositoryStore store)
            throws ConversionException, IOException {
        if (resource.byteSource().isEmpty()) return false;

        ParseContext parseContext = new ParseContext();
        parseContext.set(Locale.class, localeSupplier.get());

        // TODO Improve detection of on when Tika can actually process content...
        // parser.getSupportedTypes(parseContext) ... with TikaMediaTypeProvider ?
        if (IGNORED.contains(resource.mediaType().withoutParameters())) return false;

        var thingBuilder = store.getBuilder(resource.uri().toString());
        var iri = resource.uri().toString();
        // TODO addOrigin(resource.uri(), thingBuilder);
        Writer sw = new StringBuilderWriter();
        // TODO var thingsHandler = new XMLToThingHandler(iri, thingBuilder);
        var linksHandler = new LinkContentHandler(true);
        ContentHandler handler =
                new TeeContentHandler(
                        new BodyContentHandler(sw), linksHandler /* TODO, thingsHandler*/);

        try (var is = resource.byteSource().openBufferedStream()) {
            Metadata metadata = new Metadata();
            parser.parse(is, handler, metadata, parseContext);
            convertMetadata(metadata, thingBuilder);
            // TODO Only convertLinks IFF text/html? Or does Tika use it for other formats?
            convertLinks(linksHandler, thingBuilder, iri);

            var text = sw.toString().trim();
            if (!text.isEmpty()) thingBuilder.set("https://enola.dev/content-as-text", text);

            store.store(thingBuilder.build());
            return true;

        } catch (TikaException | SAXException e) {
            throw new ConversionException("Tika could not convert: " + resource, e);
        }
    }

    private void convertLinks(
            LinkContentHandler linksHandler, Thing.Builder<?> thing, String base) {
        ListMultimap<String, String> multimap =
                MultimapBuilder.hashKeys(13).arrayListValues().build();
        for (var link : linksHandler.getLinks()) {
            var sb = new StringBuilder(XHTMLContentHandler.XHTML.length() + 17);
            sb.append(XHTMLContentHandler.XHTML);
            sb.append('/');
            sb.append(link.getType());
            if (!Strings.isNullOrEmpty(link.getRel())) {
                sb.append('#');
                sb.append(link.getRel());
            }
            multimap.put(sb.toString(), link.getUri());
            // TODO ? link.getTitle()
        }
        multimap.asMap()
                .forEach((predicateIRI, links) -> thing.set(predicateIRI, convert(links, base)));
    }

    private ImmutableList<Link> convert(Collection<String> links, String base) {
        var baseURI = URI.create(base);
        return ImmutableList.copyOf(
                links.stream().map(link -> resolve(baseURI, link)).map(Link::new).toList());
    }

    private String resolve(URI baseURI, String link) {
        return URIs.hasScheme(link)
                ? link // TODO ? URLEncoder.encode(link, UTF_8)
                : baseURI.resolve(link).toString();
    }

    // TODO This should use Integer instead of String when appropriate, e.g. for tiff:ImageWidth.
    // TODO This needs to be smarter, but can't Tika do this itself already?!
    // - Transform https://enola.dev/tika/Exif into exif: (some are already, but many are not)
    @SuppressWarnings("Immutable")
    private void convertMetadata(Metadata metadata, Thing.Builder<?> thing) {
        final var properties = new HashMap<IRI, Object>();
        final var names = new ArrayList<>(List.of(metadata.names()));
        while (!names.isEmpty()) {
            final var name = names.remove(0);
            if (name.startsWith("X-TIKA")) continue;

            final var value =
                    metadata.isMultiValued(name)
                            ? ImmutableSet.copyOf(metadata.getValues(name))
                            : metadata.get(name);

            final var toClean = CleanMetadata.ALL.get(name);
            if (toClean != null) {
                var iri = toClean.iri();
                if (iri != null) properties.put(iri, value);

                var removeNames = List.of(toClean.removeNames());
                for (var removeName : removeNames) {
                    names.remove(removeName);
                    var removeIRI = tikaMetadataNameToEnolaIRI(removeName);
                    properties.remove(removeIRI);
                }

            } else {
                var iri = namespaceConverter.toIRI(name);
                if (!iri.equals(IRI.from(name))) properties.put(iri, value);
                else properties.put(tikaMetadataNameToEnolaIRI(name), value);
            }
        }
        properties.forEach((predicateIRI, value) -> thing.set(predicateIRI.toString(), value));
    }

    private IRI tikaMetadataNameToEnolaIRI(String name) {
        return IRI.from("https://enola.dev/tika/", URIs.encode(name));
    }
}
