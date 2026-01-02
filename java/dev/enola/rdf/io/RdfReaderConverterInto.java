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

import static com.google.common.net.MediaType.JSON_UTF_8;

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.rdf.io.RdfMediaTypeYamlLd.YAML_LD;
import static dev.enola.rdf.io.RdfMediaTypes.JSON_LD;

import com.google.common.net.MediaType;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterInto;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.yamljson.YamlJson;

import no.hasmac.jsonld.JsonLdError;
import no.hasmac.jsonld.loader.DocumentLoaderOptions;

import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

class RdfReaderConverterInto implements ConverterInto<ReadableResource, RDFHandler> {

    public static final String CONTEXT_QUERY_PARAMETER = "context";

    private final JsonLdDocumentLoader jsonLdDocumentLoader;

    public RdfReaderConverterInto(ResourceProvider rp) {
        jsonLdDocumentLoader = new JsonLdDocumentLoader(rp);
    }

    @Override
    public boolean convertInto(ReadableResource from, RDFHandler into) throws ConversionException {
        var mt = from.mediaType();
        final var originalFrom = from;
        // RDF4j doesn't dig YAML, yet; so until it supports https://json-ld.github.io/yaml-ld/
        // we just Rosetta transform YAML to JSON and then pass that through to RDF4j:
        if (MediaTypes.normalizedNoParamsEquals(mt, YAML_UTF_8)) {
            var json = new MemoryResource(from.uri(), JSON_UTF_8);
            YamlJson.YAML_TO_JSON.convertInto(from, json);
            from = json;
        } else if (MediaTypes.normalizedNoParamsEquals(mt, YAML_LD)) {
            var json = new MemoryResource(from.uri(), JSON_LD);
            YamlJson.YAML_TO_JSON.convertInto(from, json);
            from = json;
        }

        var parserFormat = Rio.getParserFormatForMIMEType(from.mediaType().toString());
        if (!parserFormat.isPresent()) {
            // Drop query, in order for Enola's "special" URL formats to work properly; such as:
            // "classpath:/picasso.yaml?context=classpath:/picasso-context.jsonld"
            var uriWithoutQueryAndFragment = URIs.dropQueryAndFragment(from.uri()).toString();
            parserFormat = Rio.getParserFormatForFileName(uriWithoutQueryAndFragment);
        }
        if (!parserFormat.isPresent())
            if (MediaTypes.normalizedNoParamsEquals(from.mediaType(), MediaType.JSON_UTF_8)) {
                parserFormat = Optional.of(RDFFormat.JSONLD);
            }
        if (parserFormat.isPresent()) {
            String baseURI = from.uri().toString();
            try (Reader reader = from.charSource().openStream()) {
                var parser = Rio.createParser(parserFormat.get());
                var config = parser.getParserConfig();

                var context = URIs.getQueryMap(from.uri()).get(CONTEXT_QUERY_PARAMETER);
                if (context != null) {
                    try {
                        var contextURI = new URI(context);
                        var opts = new DocumentLoaderOptions();
                        var document = jsonLdDocumentLoader.loadDocument(contextURI, opts);
                        config.set(JSONLDSettings.EXPAND_CONTEXT, document);

                    } catch (URISyntaxException e) {
                        throw new ConversionException("Invalid URI syntax: " + context, e);
                    } catch (JsonLdError e) {
                        throw new ConversionException(
                                "Failed loading JSON-LD Context: " + context, e);
                    }
                }

                // TODO https://github.com/eclipse-rdf4j/rdf4j/issues/5080:
                //   config.set(JSONLDSettings.DOCUMENT_LOADER, jsonLdDocumentLoader);

                // TODO Should this be made configurable via an ?preserve_bnode_ids=true, and
                // default to false? It's fine (and required) for initial testing, but later when
                // merging into a store, will they automagically get adapted to avoid dupes, or
                // not?
                config.set(BasicParserSettings.PRESERVE_BNODE_IDS, true);

                parser.setRDFHandler(into);
                parser.parse(reader, baseURI);
                return true;

            } catch (IOException e) {
                throw new ConversionException("Failing reading from : " + from, e);
            } catch (RDFParseException e) {
                var content = "";
                if (!from.equals(originalFrom)) {
                    try {
                        content = from.charSource().read();
                    } catch (IOException ex) {
                        // Ignore.
                    }
                }
                throw new ConversionException(
                        "RDFParseException reading from resource " + content,
                        from.uri(),
                        e.getLineNumber(),
                        e.getColumnNumber(),
                        e);
            }
        }
        return false;
    }
}
