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

import dev.enola.common.io.iri.IRIs;
import dev.enola.common.io.iri.NamespaceConverter;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.io.resource.URIs;
import dev.enola.thing.proto.Things;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

/**
 * {@link MetadataProvider} implementation based on looking at {@link Things}s obtained via {@link
 * ThingProvider}.
 *
 * <p>Logs errors, but does not propagate exceptions from the <tt>ThingProvider</tt>, because we do
 * not want to fail operations "just" because Metadata could not be obtained; all the methods have
 * fallbacks.
 */
public class ThingMetadataProvider implements MetadataProvider {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ThingProvider tp;
    private final NamespaceConverter ns;

    public ThingMetadataProvider(ThingProvider tp, NamespaceConverter ns) {
        this.tp = tp;
        this.ns = ns;
    }

    @Override
    public Metadata get(String iri) {
        var imageHTML = "";
        var descriptionHTML = "";
        Thing thing = null;

        try {
            thing = tp.get(iri);
            imageHTML = getImageHTML(thing);
            descriptionHTML = getDescriptionHTML(thing);

        } catch (Exception e) {
            log.warn("Could not get {}", iri, e);
        }

        return new Metadata(imageHTML, getLabel(thing, iri), descriptionHTML);
    }

    /**
     * Returns the Thing's {@link KIRI.RDFS#LABEL} or {@link KIRI.SCHEMA#NAME}, if any; otherwise
     * attempts to convert the IRI to a "CURIE", and if that also fails, it just extracts a "file
     * name" (last part of the path) from the IRI, and if that fails just returns the IRI argument
     * as-is.
     */
    private String getLabel(Thing thing, String fallbackIRI) {
        var label = getString(thing, KIRI.RDFS.LABEL);
        if (label != null) return label;

        var name = getString(thing, KIRI.SCHEMA.NAME);
        if (name != null) return name;

        if (thing != null) {
            var curie = ns.toCURIE(thing.iri());
            if (!curie.equals(thing.iri())) return curie;
        }

        try {
            return URIs.getFilename(IRIs.toURI(fallbackIRI));
        } catch (URISyntaxException e) {
            return fallbackIRI;
        }
    }

    /** Returns the Thing's {@link KIRI.SCHEMA#DESC}, if any. */
    private String getDescriptionHTML(Thing thing) {
        var description = getString(thing, KIRI.SCHEMA.DESC);
        if (description != null) return description;

        description = getString(thing, KIRI.RDFS.COMMENT);
        if (description != null) return description;

        return "";
    }

    /**
     * Returns the Thing's {@link KIRI.E#EMOJI}, if any; otherwise an HTML IMG tag using the URL
     * from {@link KIRI.SCHEMA#IMG}, if any; and if neither tries the same on the Thing's {@link
     * KIRI.RDFS#CLASS}; if that also is not present, then gives up and just an empty String.
     */
    public String getImageHTML(Thing thing) {
        if (thing == null) return "";

        var thingImage = getImageHTML_(thing);
        if (thingImage != null) return thingImage;

        var rdfClassIRI = getString(thing, KIRI.RDFS.CLASS);
        var rdfClassImage = getImageHTML_(tp.get(rdfClassIRI));
        if (rdfClassImage != null) return rdfClassImage;

        return "";
    }

    private String getImageHTML_(Thing thing) {
        if (thing == null) return null;
        var emoji = getString(thing, KIRI.E.EMOJI);
        if (emoji != null) return emoji;
        var imageURL = getString(thing, KIRI.SCHEMA.IMG);
        // TODO ImageMetadataProvider which can determine (and cache!) width & height
        if (imageURL != null) return "<img src=" + imageURL + "/>";
        return null;
    }

    private String getString(Thing thing, String propertyIRI) {
        if (thing == null) return null;
        var string = thing.getString(propertyIRI);
        if (string == null) {
            log.debug("No {} on {}:\n{}", propertyIRI, thing.iri(), thing);
        }
        return string;
    }

    @Override
    @Deprecated
    public String getLabel(String iri) {
        return get(iri).label();
    }

    @Override
    @Deprecated
    public String getDescriptionHTML(String iri) {
        return get(iri).descriptionHTML();
    }

    @Override
    @Deprecated
    public String getImageHTML(String iri) {
        return get(iri).imageHTML();
    }
}
