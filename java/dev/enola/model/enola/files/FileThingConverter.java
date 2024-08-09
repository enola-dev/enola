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
package dev.enola.model.enola.files;

import static java.nio.file.Files.readAttributes;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.MoreFileSystems;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.datatype.Datatypes;
import dev.enola.model.enola.net.Hostnames;
import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.io.ResourceIntoThingConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("rawtypes")
@AutoService(ResourceIntoThingConverter.class)
public class FileThingConverter implements ResourceIntoThingConverter<ImmutableThing> {

    private static final Logger LOG = LoggerFactory.getLogger(FileThingConverter.class);

    // TODO Use Node/File/Directory/Link instead of Thing, once files.esch.yaml is code generated...

    // TODO #high #SECURITY This probably shouldn't be auto-enabled, but gated with a --file-loader
    // This requires making ResourceIntoThingConverters no longer use ServiceLoader @AutoService.
    // Perhaps it could offer BOTH auto-loading and explicitly adding converters?

    @Override
    public Optional<List<Thing.Builder<ImmutableThing>>> convert(ReadableResource input)
            throws ConversionException {

        // TODO I'm surprised we don't have to skip *.ttl, until ResourceIntoThingConverters merges

        if (!MoreFileSystems.URI_SCHEMAS.contains(input.uri().getScheme())) return Optional.empty();

        LOG.debug("Converting {}", input);

        try {
            var node = convert(input.uri(), input.mediaType());
            // TODO How to fix stuff so that this type cast isn't required?!
            return Optional.of(List.of((Thing.Builder<ImmutableThing>) node));

        } catch (IOException | URISyntaxException e) {
            throw new ConversionException("Exception for " + input, e);
        }
    }

    private Thing.Builder<? extends ImmutableThing> convert(URI uri, MediaType mediaType)
            throws IOException, URISyntaxException {
        Path path = URIs.getFilePath(uri);
        BasicFileAttributes attrs = readAttributes(path, BasicFileAttributes.class, NOFOLLOW_LINKS);

        Thing.Builder<? extends ImmutableThing> node = ImmutableThing.builder();

        if (attrs.isRegularFile()) {
            node.set(KIRI.RDF.TYPE, File.Type_IRI);
            node.set(File.mediaType_IRI, mediaType.toString());
            node.set(File.size_IRI, attrs.size(), "https://enola.dev/UnsignedLong");

        } else if (attrs.isDirectory()) {
            node.set(KIRI.RDF.TYPE, Directory.Type_IRI);
            var childrenIRIs = new ImmutableList.Builder<URI>();
            try (var stream = Files.list(path)) {
                stream.forEach(child -> childrenIRIs.add(child.toUri()));
            }
            node.set(Directory.children_IRI, childrenIRIs.build());

        } else if (attrs.isSymbolicLink()) {
            node.set(KIRI.RDF.TYPE, Link.Type_IRI);
            var target = Files.readSymbolicLink(path);
            node.set(Link.target_IRI, target.toUri());

        } else if (attrs.isOther()) {
            // TODO Anything else of interest?!
        }

        // Common to File, Directory, Link, and Other
        setIRI(node, uri);
        node.set(Node.parent_IRI, path.getParent().toUri());
        node.set(Node.createdAt_IRI, attrs.creationTime(), Datatypes.FILE_TIME.iri());
        node.set(Node.modifiedAt_IRI, attrs.lastModifiedTime(), Datatypes.FILE_TIME.iri());
        node.set(Node.lastAccessAt_IRI, attrs.lastAccessTime(), Datatypes.FILE_TIME.iri());

        // node.set("https://enola.dev/files/Node/fileKey", attrs.fileKey().toString());

        return node;
    }

    private void setIRI(Thing.Builder<? extends ImmutableThing> node, URI uri)
            throws URISyntaxException {
        if (Strings.isNullOrEmpty(uri.getHost())) {
            var host = Hostnames.LOCAL;
            uri = new URI(uri.getScheme(), host, uri.getPath(), uri.getFragment());
        }
        node.iri(uri.toString());
    }
}
