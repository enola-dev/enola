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
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.iri.namespace.NamespaceConverter;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.thing.proto.Things;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
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
        Thing thing = null;
        try {
            thing = tp.get(iri);
        } catch (Exception e) {
            log.warn("Could not get {}", iri, e);
        }
        return get(thing, iri);
    }

    @Override
    public Metadata get(@Nullable Object object, String iri) {
        if (object instanceof Thing) return get((Thing) object, iri);
        else return get(iri);
    }

    private Metadata get(@Nullable Thing thing, @NonNull String fallbackIRI) {
        var imageHTML = getImageHTML(thing);
        var descriptionHTML = getDescriptionHTML(thing);
        var label = getLabel(thing, fallbackIRI);
        return new Metadata(imageHTML, label, descriptionHTML);
    }

    /**
     * Returns the Thing's {@link KIRI.RDFS#LABEL} or {@link KIRI.SCHEMA#NAME}, if any; otherwise
     * attempts to convert the IRI to a "CURIE", and if that also fails, it just extracts a "file
     * name" (last part of the path) from the IRI, and if that fails just returns the IRI argument
     * as-is.
     */
    private String getLabel(@Nullable Thing thing, @NonNull String fallbackIRI) {
        var label = getString(thing, KIRI.RDFS.LABEL);
        if (label != null) return label;

        var name = getString(thing, KIRI.SCHEMA.NAME);
        if (name != null) return name;

        var title = getString(thing, KIRI.DC.TITLE);
        if (title != null) return title;

        var curie = ns.toCURIE(fallbackIRI);
        if (!curie.equals(fallbackIRI)) return curie;

        try {
            var fallbackURI = IRIs.toURI(fallbackIRI);
            var filename = URIs.getFilename(fallbackURI);
            var fragment = fallbackURI.getFragment();
            if (fragment != null) return filename + "#" + fragment;
            else return filename;
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

        thingImage = getAlternativeImageHTML(thing, KIRI.RDFS.RANGE);
        if (thingImage != null) return thingImage;

        thingImage = getAlternativeImageHTML(thing, KIRI.RDFS.CLASS);
        if (thingImage != null) return thingImage;

        return "";
    }

    private String getImageHTML_(Thing thing) {
        if (thing == null) return null;
        var emoji = getString(thing, KIRI.E.EMOJI);
        if (emoji != null) return emoji;
        var imageURL = getString(thing, KIRI.SCHEMA.IMG);
        // TODO ImageMetadataProvider which can determine (and cache!) width & height
        if (imageURL != null) return "<img src=\"" + imageURL + "\" style=\"max-height: 1em;\">";
        return null;
    }

    private @Nullable String getAlternativeImageHTML(Thing thing, String viaPropertyIRI) {
        var rdfClassIRI = getString(thing, viaPropertyIRI);
        if (rdfClassIRI != null) {
            var alternativeThing = tp.get(rdfClassIRI);
            if (alternativeThing != null) {
                var alternativeImageSource = getImageHTML_(alternativeThing);
                if (alternativeImageSource != null) return alternativeImageSource;
            }
        }
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
}
