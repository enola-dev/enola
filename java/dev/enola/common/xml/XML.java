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
package dev.enola.common.xml;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class XML {

    // TODO IndentingXMLEventWriter doesn't print self-closing tags.. use SAX, after all?

    // TODO Should also order attributes of all elements alphabetically

    public static void canonicalize(ReadableResource in, WritableResource out) throws IOException {
        try (var inputStream = in.byteSource().openBufferedStream()) {
            out.charSink().write(normalizeXML(inputStream));
        } catch (ParserConfigurationException | SAXException | TransformerException e) {
            throw new IOException("XML Error: " + in, e);
        }
    }

    private static String normalizeXML(InputStream inputStream)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, true); // #security
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new InputSource(inputStream));

        normalizeWhitespace(document.getDocumentElement());

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    private static void normalizeWhitespace(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            node.setTextContent(node.getTextContent().trim());
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            normalizeWhitespace(childNodes.item(i));
        }
    }

    static void canonicalizeWithStax(ReadableResource in, WritableResource out) throws IOException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try (var inputStream = in.byteSource().openBufferedStream()) {
            XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);

            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            // NOT outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);

            try (var writer = out.charSink().openBufferedStream()) {
                XMLEventWriter eventWriter =
                        new IndentingXMLEventWriter(
                                outputFactory.createXMLEventWriter(writer), "  ");

                while (eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();

                    if (event.isCharacters()) {
                        String data = event.asCharacters().getData();
                        event = eventFactory.createCharacters(data.trim());
                    }

                    eventWriter.add(event);
                }
            }

        } catch (XMLStreamException e) {
            throw new IOException("XML error", e);
        }
    }

    private XML() {}
}
