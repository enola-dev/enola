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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

class IndentingXMLEventWriter implements XMLEventWriter {

    private static final XMLEventFactory factory = XMLEventFactory.newFactory();

    private final XMLEventWriter wrappedWriter;
    private int depth = 0;

    private boolean newLineBeforeStartElement = false;
    private boolean indentBeforeEndElement = false;

    private String indentationString = "    ";

    public IndentingXMLEventWriter(XMLEventWriter wrappedWriter, String indentation) {
        this.wrappedWriter = wrappedWriter;
        this.indentationString = indentation;
    }

    @Override
    public void close() throws XMLStreamException {
        this.wrappedWriter.close();
    }

    @Override
    public void add(XMLEvent event) throws XMLStreamException {
        switch (event.getEventType()) {
            case XMLStreamConstants.START_DOCUMENT:
                this.wrappedWriter.add(event);
                this.wrappedWriter.add(factory.createCharacters("\n"));
                break;
            case XMLStreamConstants.START_ELEMENT:
                if (this.newLineBeforeStartElement)
                    this.wrappedWriter.add(factory.createCharacters("\n"));
                this.newLineBeforeStartElement = true;
                this.indentBeforeEndElement = false;
                this.possiblyIndent();
                this.wrappedWriter.add(event);
                this.depth++;
                break;
            case XMLStreamConstants.END_ELEMENT:
                this.newLineBeforeStartElement = false;
                this.depth--;
                if (this.indentBeforeEndElement) this.possiblyIndent();
                this.indentBeforeEndElement = true;
                this.wrappedWriter.add(event);
                this.wrappedWriter.add(factory.createCharacters("\n"));
                break;
            case XMLStreamConstants.COMMENT:
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                this.wrappedWriter.add(event);
                this.wrappedWriter.add(factory.createCharacters("\n"));
                this.newLineBeforeStartElement = false;
                this.indentBeforeEndElement = true;
                break;
            default:
                this.wrappedWriter.add(event);
        }
    }

    private void possiblyIndent() throws XMLStreamException {
        if (this.depth > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.depth; i++) sb.append(this.indentationString);
            this.wrappedWriter.add(factory.createCharacters(sb.toString()));
        }
    }

    @Override
    public void add(XMLEventReader reader) throws XMLStreamException {
        this.wrappedWriter.add(reader);
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return this.wrappedWriter.getPrefix(uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.wrappedWriter.setPrefix(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.wrappedWriter.setDefaultNamespace(uri);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.wrappedWriter.getNamespaceContext();
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.wrappedWriter.setNamespaceContext(context);
    }

    @Override
    public void flush() throws XMLStreamException {
        this.wrappedWriter.flush();
    }

    public void setIndentationString(String indentationString) {
        this.indentationString = indentationString;
    }
}
