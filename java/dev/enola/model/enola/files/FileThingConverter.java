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
package dev.enola.model.enola.files;

import static java.nio.file.Files.readAttributes;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import com.google.common.primitives.UnsignedLong;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.MoreFileSystems;
import dev.enola.common.io.iri.URIs;
import dev.enola.model.enola.Datatypes;
import dev.enola.thing.KIRI;
import dev.enola.thing.io.UriIntoThingConverter;
import dev.enola.thing.repo.ThingRepositoryStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/** FileThingConverter converts <code>file:/</code> URI into File &amp; Directory Things. */
public class FileThingConverter implements UriIntoThingConverter {

    private static final Logger LOG = LoggerFactory.getLogger(FileThingConverter.class);

    // TODO Use Node/File/Directory/Link instead of Thing, once files.esch.yaml is code generated...

    // TODO #high #SECURITY This probably shouldn't be auto-enabled, but gated with a --file-loader
    // This requires making UriIntoThingConverters no longer use ServiceLoader @AutoService.
    // Perhaps it could offer BOTH auto-loading and explicitly adding converters?

    @Override
    public boolean convertInto(URI input, ThingRepositoryStore into)
            throws ConversionException, IOException {

        // TODO I'm surprised we don't have to skip *.ttl, until UriIntoThingConverters merges

        if (!MoreFileSystems.URI_SCHEMAS.contains(input.getScheme())) return false;

        LOG.debug("Converting {}", input);

        try {
            convert(input, into);
            return true;

        } catch (URISyntaxException e) {
            throw new ConversionException("Exception for " + input, e);
        }
    }

    private void convert(URI uri, ThingRepositoryStore into)
            throws IOException, URISyntaxException {
        Path path = URIs.getFilePath(uri);
        BasicFileAttributes attrs = readAttributes(path, BasicFileAttributes.class, NOFOLLOW_LINKS);

        var node = into.getBuilder(getIRI(uri));
        addOrigin(uri, node);

        if (attrs.isRegularFile()) {
            node.set(KIRI.RDF.TYPE, File.Type_IRI);
            // node.set(File.mediaType_IRI, mediaType.toString());
            node.set(
                    File.size_IRI,
                    UnsignedLong.valueOf(attrs.size()),
                    "https://enola.dev/UnsignedLong");

        } else if (attrs.isDirectory()) {
            node.set(KIRI.RDF.TYPE, Directory.Type_IRI);
            // TODO Re-enable!
            // var childrenIRIs = new ImmutableList.Builder<URI>();
            // try (var stream = Files.list(path)) {
            //    stream.forEach(child -> childrenIRIs.add(child.toUri()));
            // }
            // node.set(Directory.children_IRI, childrenIRIs.build());

        } else if (attrs.isSymbolicLink()) {
            node.set(KIRI.RDF.TYPE, Link.Type_IRI);
            var target = Files.readSymbolicLink(path);
            node.set(Link.target_IRI, target.toUri());

        } else if (attrs.isOther()) {
            // TODO Anything else of interest?!
        }

        // Common to File, Directory, Link, and Other
        // TODO Re-enable File (or Directory) parent!
        // (This was temporarily disabled in order to re-enable mkdocs --strict.)
        // node.set(Node.parent_IRI, path.getParent().toUri());

        node.set(
                Node.createdAt_IRI,
                attrs.creationTime(),
                dev.enola.model.enola.Datatypes.FILE_TIME.iri());
        node.set(Node.modifiedAt_IRI, attrs.lastModifiedTime(), Datatypes.FILE_TIME.iri());
        node.set(Node.lastAccessAt_IRI, attrs.lastAccessTime(), Datatypes.FILE_TIME.iri());

        // node.set("https://enola.dev/files/Node/fileKey", attrs.fileKey().toString());

        into.store(node.build());
    }

    private String getIRI(URI uri) throws URISyntaxException {
        // TODO Uncomment adding Hostnames.LOCAL as file: authority (and in *Test)
        // The problem is that this needs to "match" KIRI.E.ORIGIN in UriIntoThingConverters
        //
        // if (Strings.isNullOrEmpty(uri.getHost())) {
        //    var host = Hostnames.LOCAL;
        //    uri = new URI(uri.getScheme(), host, uri.getPath(), uri.getFragment());
        // }
        return uri.toString();
    }
}
