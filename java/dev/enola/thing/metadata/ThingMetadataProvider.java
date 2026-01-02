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
package dev.enola.thing.metadata;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.data.iri.NamespaceConverter;
import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.proto.Things;
import dev.enola.thing.repo.ThingProvider;
import dev.enola.thing.template.Templates;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;

/**
 * {@link MetadataProvider} implementation based on looking at {@link Things}s obtained via {@link
 * ThingProvider}; see also related <a href="https://docs.enola.dev/concepts/metadata/">end-user
 * documentation</a>.
 *
 * <p>Logs errors, but does not propagate exceptions from the <code>ThingProvider</code>, because we
 * do not want to fail operations "just" because Metadata could not be obtained; all the methods
 * have fallbacks.
 */
public class ThingMetadataProvider implements MetadataProvider<Thing> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ThingProvider tp;
    private final NamespaceConverter ns;

    public ThingMetadataProvider(ThingProvider tp, NamespaceConverter ns) {
        this.tp = tp;
        this.ns = ns;
    }

    public Metadata get(Thing thing) {
        return get(thing, thing.iri());
    }

    @Override
    public Metadata get(String iri) {
        Thing thing = null;
        try {
            // TODO We should call for "internal" but skip for ext - but how to distinguish, here?!
            if (!Templates.hasVariables(iri)) thing = tp.get(iri);
        } catch (Exception e) {
            log.warn("Failed to get {}", iri, e);
        }
        return get(thing, iri);
    }

    @Override
    public Metadata get(@Nullable Thing thing, String fallbackIRI) {
        var imageURL = getImageURL_(thing);
        if (imageURL == null) imageURL = "";

        var emoji = getEmoji_(thing);
        if (emoji == null) emoji = "";

        var imageHTML = getImageHTML(thing);
        var descriptionHTML = getDescriptionHTML(thing);
        var curie = ns.toCURIE(fallbackIRI);
        var label = getLabel(thing, curie, fallbackIRI);
        var curieIfDifferentFromFallbackIRI = !curie.equals(fallbackIRI) ? curie : "";
        return new Metadata(
                fallbackIRI,
                imageHTML,
                imageURL,
                emoji,
                curieIfDifferentFromFallbackIRI,
                label,
                descriptionHTML);
    }

    /**
     * Returns the Thing's {@link KIRI.RDFS#LABEL} or {@link KIRI.SCHEMA#NAME}, if any; otherwise
     * attempts to convert the IRI to a "CURIE", and if that also fails, it just extracts a "file
     * name" (last part of the path) from the IRI, and if that fails just returns the IRI argument
     * as-is.
     */
    private String getLabel(@Nullable Thing thing, String curie, String fallbackIRI) {
        var label = getLabel_(thing);
        if (label != null) return label;

        label = getAlternative(thing, KIRI.RDF.TYPE, type -> getLabelViaProperty(thing, type));
        if (label != null) return label;

        label = getAlternative(thing, KIRI.RDFS.RANGE, range -> getLabel_(range));
        if (label != null) return label;

        if (!curie.equals(fallbackIRI)) return curie;

        try {
            var fallbackURI = new URI(fallbackIRI);
            var filename = URIs.getFilenameOrLastPathSegmentOrHost(fallbackURI);
            if (filename == null) return fallbackIRI;

            // TODO Should we consider any ?query=arg as part of a "label"?!
            var fragment = fallbackURI.getFragment();
            if (fragment != null) return filename + "#" + fragment;
            else return filename;
        } catch (URISyntaxException e) {
            return fallbackIRI;
        }
    }

    private @Nullable String getLabelViaProperty(@Nullable Thing thing, Thing type) {
        if (thing == null) return null;
        var typesLabelProperty = type.getString(KIRI.E.LABEL_PROPERTY);
        if (typesLabelProperty == null) return null;
        return thing.getString(typesLabelProperty);
    }

    private @Nullable String getLabel_(@Nullable Thing thing) {
        var label = getString(thing, KIRI.E.LABEL);
        if (label != null) return label;

        label = getString(thing, KIRI.RDFS.LABEL);
        if (label != null) return label;

        var name = getString(thing, KIRI.SCHEMA.NAME);
        if (name != null) return name;

        var title = getString(thing, KIRI.DC.TITLE);
        return title;
    }

    /** Returns the Thing's {@link KIRI.SCHEMA#DESC}, if any. */
    private String getDescriptionHTML(Thing thing) {
        var description = getString(thing, KIRI.E.DESCRIPTION);
        if (description != null) return description;

        description = getString(thing, KIRI.SCHEMA.DESC);
        if (description != null) return description;

        description = getString(thing, KIRI.SCHEMA.ABSTRACT);
        if (description != null) return description;

        description = getString(thing, KIRI.DC.DESCRIPTION);
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
    private String getImageHTML(Thing thing) {
        if (thing == null) return "";

        var thingImage = getImageHTML_(thing);
        if (thingImage != null) return thingImage;

        return "";
    }

    private @Nullable String getImageHTML_(Thing thing) {
        if (thing == null) return null;

        var emoji = getEmoji_(thing);
        if (emoji != null) return emoji;

        var imageURL = getImageURL_(thing);
        if (imageURL != null) return html(imageURL);

        // TODO Also support (and test) https://schema.org/ImageObject
        // for https://schema.org/thumbnail and https://schema.org/logo

        return null;
    }

    private @Nullable String getEmoji_(Thing thing) {
        var emoji = getString(thing, KIRI.E.EMOJI);
        if (emoji != null) return emoji;

        emoji = getAlternative(thing, KIRI.RDFS.RANGE, range -> getEmoji_(range));
        if (emoji != null) return emoji;

        emoji = getAlternative(thing, KIRI.RDF.TYPE, type -> getEmoji_(type));
        return emoji;
    }

    private @Nullable String getImageURL_(Thing thing) {
        var imageURL = getAlternative(thing, KIRI.RDFS.RANGE, range -> getImageURL__(range));
        if (imageURL != null) return imageURL;

        imageURL = getAlternative(thing, KIRI.RDF.TYPE, type -> getImageURL__(type));
        return imageURL;
    }

    private @Nullable String getImageURL__(Thing thing) {
        var imageURL = getString(thing, KIRI.SCHEMA.LOGO);
        if (imageURL != null) return html(imageURL);

        imageURL = getString(thing, KIRI.SCHEMA.THUMBNAIL_URL);
        if (imageURL != null) return html(imageURL);

        imageURL = getString(thing, KIRI.SCHEMA.IMG);
        if (imageURL != null) return html(imageURL);

        return null;
    }

    private String html(String imageURL) {
        // TODO ImageMetadataProvider which can determine (and cache!) width & height
        return "<img src=\"" + imageURL + "\" style=\"max-height: 1em;\">";
    }

    private @Nullable String getAlternative(
            Thing thing, String viaPropertyIRI, Function<Thing, String> alt) {
        var alternativeThingIRI = getString(thing, viaPropertyIRI);
        if (alternativeThingIRI != null && !alternativeThingIRI.equals(thing.iri())) {
            var alternativeThing = tp.get(alternativeThingIRI);
            if (alternativeThing != null) {
                var alternativeSource = alt.apply(alternativeThing);
                return alternativeSource;
            }
        }
        return null;
    }

    private @Nullable String getString(@Nullable Thing thing, String propertyIRI) {
        if (thing == null) return null;
        String string = null;
        if (!thing.isIterable(propertyIRI)) string = thing.getString(propertyIRI);
        // TODO Implement supporting e.g. multiple types
        if (string == null) {
            log.trace("No {} on {}:\n{}", propertyIRI, thing.iri(), thing);
        }
        return string;
    }
}
