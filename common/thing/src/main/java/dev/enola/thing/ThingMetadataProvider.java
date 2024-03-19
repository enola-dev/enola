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
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.io.resource.URIs;
import dev.enola.thing.proto.Things;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * {@link MetadataProvider} implementation based on looking at {@link Things}s obtained via {@link
 * ThingProvider}.
 *
 * <p>Logs logs but does not propagate exceptions from the <tt>ThingProvider</tt>, because we do not
 * want to fail operations "just" because Metadata could not be obtained; all the methods have
 * fallbacks.
 */
public class ThingMetadataProvider implements MetadataProvider {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ThingProvider tp;

    public ThingMetadataProvider(ThingProvider tp) {
        this.tp = tp;
    }

    /**
     * Returns the Thing's {@link KIRI.SCHEMA#ID}, if any; otherwise just returns the IRI argument.
     */
    @Override
    public String getID(String iri) {
        var id = getString(iri, KIRI.SCHEMA.ID);
        if (id != null) return id;
        return iri;
    }

    /**
     * Returns the Thing's {@link KIRI.RDFS.LABEL} or {@link KIRI.SCHEMA#NAME}, if any; otherwise
     * attempts to extract a "file name" (last part of the path) from the IRI, and if that fails
     * just returns the IRI argument as-is.
     */
    @Override
    public String getLabel(String iri) {
        var label = getString(iri, KIRI.RDFS.LABEL);
        if (label != null) return label;

        var name = getString(iri, KIRI.SCHEMA.NAME);
        if (name != null) return name;

        try {
            return URIs.getFilename(IRIs.toURI(iri));
        } catch (URISyntaxException e) {
            return iri;
        }
    }

    /** Returns the Thing's {@link KIRI.SCHEMA#DESC}, if any. */
    @Override
    public String getDescriptionHTML(String iri) {
        return getString(iri, KIRI.SCHEMA.DESC);
    }

    /**
     * Returns the Thing's {@link KIRI.E#EMOJI}, if any; otherwise a HTML IMG tag using the URL from
     * {@link KIRI.SCHEMA#IMG}, if any; and if neither tries the same on the Thing's {@link
     * KIRI.RDFS#CLASS}; if that also is not present, then gives up and just an empty String.
     */
    @Override
    public String getImageHTML(String iri) {
        var thingImage = getImageHTML_(iri);
        if (thingImage != null) return thingImage;

        var rdfClassIRI = getString(iri, KIRI.RDFS.CLASS);
        var rdfClassImage = getImageHTML_(rdfClassIRI);
        if (rdfClassImage != null) return rdfClassImage;

        return "";
    }

    private String getImageHTML_(String iri) {
        var emoji = getString(iri, KIRI.E.EMOJI);
        if (emoji != null) return emoji;
        var imageURL = getString(iri, KIRI.SCHEMA.IMG);
        // TODO ImageMetadataProvider which can determine (and cache!) width & height
        if (imageURL != null) return "<img src=" + imageURL + "/>";
        return null;
    }

    private String getString(String thingIRI, String propertyIRI) {
        try {
            return ThingExt.getString(tp.getThing(thingIRI), propertyIRI);
        } catch (IOException e) {
            log.warn("Could not get {} from {}", propertyIRI, thingIRI);
            return null;
        }
    }
}
