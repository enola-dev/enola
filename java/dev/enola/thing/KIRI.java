/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
    // TODO Convert all of this into stuff like dev.enola.model.w3.rdfs.Class
    // TODO This class should eventually disappear from package dev.enola.thing

    // The JavaDoc of this class must currently be manually kept in sync with
    // the same content that's also in the //models/enola.dev/properties.ttl file.
    // (We could theoretically build something like https://github.com/ansell/rdf4j-schema-generator
    // into Enola to automate that; but it's probably not really worth it.)

    // TODO Make all of these actually resolvable in Enola! (Just for documentation look-up.)
    // by (built-in?!) loading //models/enola.dev/properties.ttl from classpath at start-up

    // TODO LDP? https://rdf4j.org/javadoc/latest/org/eclipse/rdf4j/model/vocabulary/LDP.html

    /** Enola.dev's very own! */
    // NB: Should the constant values ever change, update //models/enola.dev/properties.ttl
    public static final class E {
        private static final String NS = "https://enola.dev/";

        // TODO Change to e.g. https://enola.dev/ql/all?inline=true&limit=7
        // TODO "enola:/?inline" would be nicer than "enola:/inline" but fails to match
        public static final String LIST_THINGS = "enola:/inline";
        public static final String LIST_IRIS = "enola:/";

        // https://docs.enola.dev/concepts/metadata/
        public static final String LABEL = NS + "label";
        public static final String DESCRIPTION = NS + "description";

        /**
         * Emoji 😃 of a Thing, from Unicode or <a href="https://www.nerdfonts.com">Nerdfonts</a>.
         *
         * <p>Often used as an alternative to {@link KIRI.SCHEMA#IMG}, and (either) is often shown
         * as prefix to the label in UIs.
         */
        public static final String EMOJI = NS + "emoji";

        public static final String MEDIA_TYPE = NS + "mediaType";

        /**
         * URI of what something is 'based on', e.g. where it 'comes from' (source), such as where
         * e.g. a Thing was originally "loaded" from. This may be a list.
         *
         * <p>TODO: Is there some existing standard vocabulary for this?
         */
        public static final String ORIGIN = NS + "origin";

        public static final String IRI_TEMPLATE_PROPERTY = NS + "iriTemplate";
        public static final String IRI_TEMPLATE_DATATYPE = NS + "IRITemplate";
        public static final String LABEL_PROPERTY = NS + "labelProperty";

        // Special
        public static final String UNKNOWN_CLASS = NS + "UnknownClass"; // TODO UnknownClass.IRI

        // Style-related stuff...
        public static final String COLOR = NS + "color";
        public static final String TEXT_COLOR = NS + "text-color";
    }

    /** Schema.org Properties. */
    public static final class SCHEMA {
        private static final String NS = "https://schema.org/";

        /**
         * IRI which identifies 🆔 a Thing, see https://schema.org/identifier. This is a "logical"
         * identity, and may or may not be an URL.
         */
        // TODO This is technically not always an IRI... so make it E.GUN, instead!
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

        /** An abstract is a short description that summarizes a CreativeWork. */
        public static final String ABSTRACT = NS + "abstract";

        /**
         * URL of an 🖼️ image of the Thing, see https://schema.org/image.
         *
         * <p>Perhaps e.g. a logo or favicon or something like that. Alternatively use {@link
         * KIRI.E#EMOJI}.
         */
        public static final String IMG = NS + "image";

        public static final String THUMBNAIL_URL = NS + "thumbnailUrl";

        public static final String LOGO = NS + "logo";

        /**
         * IRI of Property for URL 🔗 of the Thing, see https://schema.org/url. You *CAN* always
         * http GET an URL. This is NOT the same as a logical URI/IRI, and thus not be to confused
         * with the {@link #ID}. One example of this could be e.g. its use in Thing "metadata" about
         * a file: URL; this would point to the actual file itself.
         */
        public static final String URL = NS + "url";

        /** IRI of URL Datatype. Used to mark properties which are links to webpages. */
        public static final String URL_DATATYPE = NS + "URL";

        /**
         * IRI of a Thing which is "the 🪞 same as this one", see https://schema.org/sameAs. For
         * example, the URL of a Wikipedia article about it.
         */
        public static final String SAMEAS = NS + "sameAs";

        private SCHEMA() {}
    }

    /** Dublin Core */
    public static final class DC {
        private static final String NS = "http://purl.org/dc/elements/1.1/";

        public static final String TITLE = NS + "title";
        public static final String DESCRIPTION = NS + "description";

        public static final String CREATOR = NS + "creator";
        public static final String LANGUAGE = NS + "language";

        private DC() {}
    }

    public static final class RDF {
        private static final String NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        /**
         * IRI of the Type :information_source: 🛈 of a Thing; see
         * https://www.w3.org/TR/rdf-schema/#ch_type.
         */
        public static final String TYPE = NS + "type";

        public static final String PROPERTY = NS + "Property";

        /** 📃 */
        public static final String HTML = NS + "HTML";

        public static final String JSON = NS + "JSON";

        private RDF() {}
    }

    public static final class RDFS {
        private static final String NS = "http://www.w3.org/2000/01/rdf-schema#";

        /** https://www.w3.org/TR/rdf-schema/#ch_class */
        public static final String CLASS = NS + "Class";

        /** https://www.w3.org/TR/rdf-schema/#ch_label */
        public static final String LABEL = NS + "label";

        /** https://www.w3.org/TR/rdf-schema/#ch_comment */
        public static final String COMMENT = NS + "comment";

        /** https://www.w3.org/TR/rdf-schema/#ch_domain */
        public static final String DOMAIN = NS + "domain";

        /** https://www.w3.org/TR/rdf-schema/#ch_range */
        public static final String RANGE = NS + "range";

        private RDFS() {}
    }

    /**
     * XML Schema's built-in datatypes. These are used as Things' Literal's Datatypes in {@link
     * Literal#getDatatype()}. See <a href="https://www.w3.org/TR/rdf11-concepts/#xsd-datatypes">RDF
     * 1.1 Concepts XSD datatypes</a>, based (of course) on the <a
     * href="https://www.w3.org/TR/xmlschema11-2/">XML Schema 1.1 datatypes</a>.
     */
    // TODO One fine day this should be generated
    public static final class XSD {
        private static final String NS = "http://www.w3.org/2001/XMLSchema#";

        public static final String STRING = NS + "string";
        public static final String IRI = NS + "anyURI";
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
