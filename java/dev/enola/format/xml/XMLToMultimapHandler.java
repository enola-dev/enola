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

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import dev.enola.common.io.iri.namespace.NamespaceRepository;
import dev.enola.common.io.iri.namespace.NamespaceRepositoryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class XMLToMultimapHandler extends DefaultHandler {

    // TODO Ditch Multimap approach, and just try a DOM-based take!
    // because Multimap does NOT "preserve order" (only "by IRI")

    // TODO Move from dev.enola.format.xml to dev.enola.common.xml ?

    // TODO Make this "streaming" by calling out to a JSON-B/P/?? Handler

    private static final Logger LOG = LoggerFactory.getLogger(XMLToMultimapHandler.class);

    public static final String TEXT_PROPERTY_IRI = "https://enola.dev/text";
    public static final String NODES_PROPERTY_IRI = "https://enola.dev/nodes";

    private final String defaultNamespaceIRI;
    private final NamespaceRepositoryBuilder nrb = new NamespaceRepositoryBuilder();
    private final Deque<ListMultimap<String, Object>> multimaps = new ArrayDeque<>();

    @SuppressWarnings("unchecked")
    public XMLToMultimapHandler(String defaultNamespaceIRI) {
        this.defaultNamespaceIRI = defaultNamespaceIRI;
        multimaps.add(ArrayListMultimap.create());
    }

    // TODO This actually isn't used anywhere - yet?
    public NamespaceRepository getNamespaces() {
        return nrb.build();
    }

    public Optional<ListMultimap<String, Object>> getRoot() {
        if (multimaps.size() != 1) throw new IllegalStateException(multimaps.toString());
        return Optional.of(multimaps.peek());
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
        nrb.store(prefix, uri);
    }

    private String iri(String uri, String localName, String qName) {
        if (Strings.isNullOrEmpty(uri)) uri = defaultNamespaceIRI;
        if (Strings.isNullOrEmpty(localName)) throw new IllegalStateException(uri + " " + qName);
        if (!(uri.endsWith("/") || uri.endsWith("#"))) return uri + "/" + localName;
        else return uri + localName;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        ListMultimap<String, Object> multimap = ArrayListMultimap.create();
        multimaps.add(multimap);

        for (int i = 0; i < attributes.getLength(); i++) {
            var attributeURI = attributes.getURI(i);
            var attributeLocalName = attributes.getLocalName(i);
            var attributeQName = attributes.getQName(i);
            var attributeValue = attributes.getValue(i);
            // TODO ? var attributeType = attributes.getType(i);
            var attributeIRI = iri(attributeURI, attributeLocalName, attributeQName);
            LOG.trace(
                    "attribute #{}: uri={}, localName={}, qName={}; value={}",
                    i,
                    attributeURI,
                    attributeLocalName,
                    attributeQName,
                    attributeValue);
            multimap.put(attributeIRI, attributeValue);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        var nested = multimaps.removeLast();
        if (!nested.isEmpty()) multimaps.getLast().put(iri(uri, localName, qName), nested);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        var text = new String(ch, start, length).trim();
        if (!text.isEmpty()) {
            var thingBuilder = multimaps.getLast();
            thingBuilder.put(TEXT_PROPERTY_IRI, text);
        }
    }
}
