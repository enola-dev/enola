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

import dev.enola.common.io.iri.namespace.NamespaceRepository;
import dev.enola.common.io.iri.namespace.NamespaceRepositoryBuilder;
import dev.enola.thing.Thing;
import dev.enola.thing.impl.IImmutablePredicatesObjects;
import dev.enola.thing.impl.ImmutablePredicatesObjects;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * XML SAX {@link ContentHandler} which transforms XML into a {@link dev.enola.thing.Thing}.
 *
 * <p>Nota bene: This is simplistic! It works for "property-style" XML, but not (well) for others.
 *
 * <p>This is NOT thread-safe!
 */
public class XMLToThingHandler extends DefaultHandler {

    // TODO Consider implementing this via & through the existing JSON[-LD, ctx?] support instead?

    private static final Logger LOG = LoggerFactory.getLogger(XMLToThingHandler.class);

    private final String defaultNamespaceIRI;
    private final NamespaceRepositoryBuilder nrb = new NamespaceRepositoryBuilder();

    private @Nullable String previousElementQName;
    private final Deque<IImmutablePredicatesObjects.Builder<IImmutablePredicatesObjects>>
            thingBuilders = new ArrayDeque<>();
    private final Deque<String> propertyIRIs = new ArrayDeque<>();

    @SuppressWarnings("unchecked")
    public XMLToThingHandler(String baseIRI, Thing.Builder<?> thingBuilder) {
        this(
                baseIRI,
                (IImmutablePredicatesObjects.Builder<IImmutablePredicatesObjects>) thingBuilder);
    }

    public XMLToThingHandler(
            String defaultNamespaceIRI,
            IImmutablePredicatesObjects.Builder<IImmutablePredicatesObjects> thingBuilder) {
        thingBuilders.push(thingBuilder);
        this.defaultNamespaceIRI = defaultNamespaceIRI;
    }

    public NamespaceRepository getNamespaces() {
        return nrb.build();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
        nrb.store(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) {
        // Ignore.
    }

    private String iri(String uri, String localName, String qName) {
        if (Strings.isNullOrEmpty(uri)) uri = defaultNamespaceIRI;
        if (Strings.isNullOrEmpty(localName)) throw new IllegalStateException(uri + " " + qName);
        return uri + "/" + localName;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        LOG.trace("startElement: uri={}, localName={}, qName={}", uri, localName, qName);
        if (Strings.isNullOrEmpty(qName)) throw new IllegalStateException(localName);
        if (!qName.equals(previousElementQName)) {
            // Start nested element
        } else {
            // Start same level element
        }
        previousElementQName = qName;

        var nested = ImmutablePredicatesObjects.builder();
        thingBuilders.add(nested);

        var propertyIRI = iri(uri, localName, qName);
        propertyIRIs.add(propertyIRI);

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
            nested.set(attributeIRI, attributeValue);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        LOG.trace("endElement: uri={}, localName={}, qName={}", uri, localName, qName);
        if (Strings.isNullOrEmpty(qName)) throw new IllegalStateException(localName);
        if (!qName.equals(previousElementQName)) {
            // End nested element

        } else {
            // End same level element
            // NOOP.
        }

        var nested = thingBuilders.removeLast().build();
        if (!nested.predicateIRIs().isEmpty())
            thingBuilders.getLast().set(iri(uri, localName, qName), nested);

        propertyIRIs.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String text = new String(ch, start, length).trim();
        String propertyIRI = propertyIRIs.getLast();

        if (!text.isEmpty()) {
            var thingBuilder = thingBuilders.getLast();
            // TODO thingBuilder.get(propertyIRI) check if already set...
            thingBuilder.set(propertyIRI, text);
        }
    }
}
