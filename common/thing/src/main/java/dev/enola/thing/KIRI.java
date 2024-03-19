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
package dev.enola.thing;

import dev.enola.thing.proto.Value.Literal;

/**
 * Java constants for some "well-known" IRIs used in Enola's code. This is NOT a list of all
 * possible such IRIs, because that's by definition an open set. This is merely for convenient use
 * in code.
 */
public final class KIRI {

    // TODO Make all of these actually resolvable in Enola! (Just for documentation look-up.)

    // TODO LDP? https://rdf4j.org/javadoc/latest/org/eclipse/rdf4j/model/vocabulary/LDP.html

    /** Enola.dev's very own! */
    public static final class E {
        private static final String NS = "https://enola.dev/";

        /**
         * Emoji 😃 of a Thing, from Unicode or <a href="https://www.nerdfonts.com">Nerdfonts</a>.
         *
         * <p>Often used as an alternative to {@link KIRI.SORG#IMG}, and (either) is often shown as
         * prefix to the label in UIs. This
         */
        public static final String EMOJI = NS + "emoji";
    }

    /** Schema.org Properties. */
    public static final class SORG {
        private static final String NS = "https://schema.org/";

        /**
         * IRI which identifies 🆔 a Thing, see https://schema.org/identifier. This is a "logical"
         * identity, and may or may not be an URL.
         */
        public static final String ID = NS + "identifier";

        /**
         * Text with human-readable name (AKA "label" 🏷️) of a Thing, see https://schema.org/name.
         */
        public static final String NAME = NS + "name";

        /**
         * Text with human-readable 📜 description of a Thing, see https://schema.org/description.
         *
         * <p>Typically length is a sentence or single paragraph.
         */
        public static final String DESC = NS + "description";

        /**
         * URL of an 🖼️ image of the Thing, see https://schema.org/image.
         *
         * <p>Perhaps e.g. a logo or favicon or something like that. Alternatively use {@link
         * KIRI.E#EMOJI}.
         */
        public static final String IMG = NS + "image";

        /**
         * URL 🔗 of the Thing, see https://schema.org/url. You *CAN* always http GET an URL. This
         * is NOT the same as a logical URI/IRI, and thus not be to confused with the {@link #ID}.
         * One example of this could be e.g. its use in Thing "metadata" about a file: URL; this
         * would point to the actual file itself.
         */
        public static final String URL = NS + "url";

        /**
         * IRI of a Thing which is "the 🪞 same as this one", see https://schema.org/sameAs. For
         * example, the URL of a Wikipedia article about it.
         */
        public static final String SAMEAS = NS + "sameAs";

        private SORG() {}
    }

    public static final class RDF {
        private static final String NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        /**
         * IRI of the Type :information_source: 🛈 of a Thing; see
         * https://www.w3.org/TR/rdf-schema/#ch_type.
         */
        public static final String TYPE = NS + "type";

        /** 📃 */
        public static final String HTML = NS + "HTML";

        public static final String JSON = NS + "JSON";

        private RDF() {}
    }

    public static final class RDFS {
        private static final String NS = "http://www.w3.org/2000/01/rdf-schema#";

        /** https://www.w3.org/TR/rdf-schema/#ch_class */
        public static final String CLASS = NS + "Class";

        // Intentionally no LABEL here, just use SORG.NAME! (It's a subproperty of rdfs:label.)
        // https://www.w3.org/TR/rdf-schema/#ch_label

        // Intentionally no COMMENT here, just use SORG.DESC!
        // https://www.w3.org/TR/rdf-schema/#ch_comment

        private RDFS() {}
    }

    /**
     * XML Schema's built-in datatypes. These are used as Things' Literal's Datatypes in {@link
     * Literal#getDatatype()}. See <a href="https://www.w3.org/TR/rdf11-concepts/#xsd-datatypes">RDF
     * 1.1 Concepts XSD datatypes</a>, based (of course) on the <a
     * href="https://www.w3.org/TR/xmlschema11-2/">XML Schema 1.1 datatypes</a>.
     */
    public static final class XSD {
        private static final String NS = "http://www.w3.org/2001/XMLSchema#";

        public static final String BOOL = NS + "boolean";

        public static final String DOUBLE = NS + "double";
        public static final String FLOAT = NS + "float";

        public static final String INT32 = NS + "int";
        public static final String INT64 = NS + "long";
        public static final String UINT64 = NS + "unsignedLong";
        public static final String UINT32 = NS + "unsignedInt";

        public static final String BIN64 = NS + "base64Binary";

        public static final String TS = NS + "dateTime";

        private XSD() {}
    }

    private KIRI() {}
}
