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
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class XML {

    // TODO Should also order attributes of all elements alphabetically

    // TODO Could #later re-implement this with StAX or SAX instead of DOM, for less memory use? But
    // DOM is just easier to manipulate in code - and StAX is a PITA lacking a built-in indenting
    // formatter.

    public static void canonicalize(ReadableResource in, WritableResource out, boolean format)
            throws IOException {
        try (var inputStream = in.byteSource().openBufferedStream()) {
            out.charSink().write(normalizeXML(inputStream, format));
        } catch (ParserConfigurationException | SAXException | TransformerException e) {
            throw new IOException("XML Error: " + in, e);
        }
    }

    private static String normalizeXML(InputStream inputStream, boolean format)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {

        // TODO Use streaming SAX instead of DOM; and break this up... use XmlResourceParser

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); // #security
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new InputSource(inputStream));

        normalizeWhitespace(document.getDocumentElement());

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        if (format) transformer.setOutputProperty(OutputKeys.INDENT, "yes");

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

    private XML() {}
}
