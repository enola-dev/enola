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

import com.google.common.collect.Collections2;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.datatype.DatatypeRepository;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;

// package-local
class ObjectConversions {

    static <T> Optional<T> as(
            Object object,
            Class<T> klass,
            PredicatesObjects predicatesObjects,
            String predicateIRI) {
        if (object == null) return Optional.empty();
        if (klass.isInstance(object)) return Optional.of((T) object);
        if (String.class.equals(klass)) {
            if (object instanceof Literal literal) return Optional.of((T) literal.value());
            if (object instanceof URI uri) return Optional.of((T) uri.toString());
            if (object instanceof Link link) return Optional.of((T) link.iri());
            if (object instanceof Collection<?> collection)
                return Optional.of(
                        (T) Collections2.transform(collection, e -> e.toString()).toString());
            // TODO Ideally, it should look up the "right" text, using a Lang Ctx Key from the TLC
            if (object instanceof LangString langString) return Optional.of((T) langString.text());
        }
        try {
            var dtIRI = datatypeLEGACY(predicateIRI, predicatesObjects);
            // TODO Find Datatype via object Java class lookup in DatatypeRepository?
            if (dtIRI == null)
                throw new IllegalStateException(
                        predicateIRI
                                + " has no Datatype; cannot convert "
                                + object
                                + " of "
                                + object.getClass()
                                + " to "
                                + klass);
            var dtr = TLC.get(DatatypeRepository.class);
            var dt = dtr.get(dtIRI);
            if (dt == null)
                throw new IllegalStateException(
                        dtIRI
                                + " not found; cannot convert "
                                + object
                                + " of "
                                + object.getClass()
                                + " to "
                                + klass);
            // TODO Make this more generic so it can support klass other than String
            var opt = dt.stringConverter().convertObjectToType(object, klass);
            if (opt.isEmpty())
                throw new IllegalStateException(
                        object + " of " + object.getClass() + " to " + klass);
            return opt;

        } catch (IOException e) {
            // TODO Get rid of throws IOException and remove this.
            // Or better log any exceptions and return just Optional.empty()?
            throw new ConversionException("Failed to convert " + object + " to " + klass, e);
        }
    }

    @Deprecated // TODO Remove once record Literal is gone
    private static @Nullable String datatypeLEGACY(
            String predicateIRI, PredicatesObjects predicatesObjects) {
        var datatype = predicatesObjects.datatype(predicateIRI);
        if (datatype != null) return datatype;

        var object = predicatesObjects.get(predicateIRI);
        if (object != null && object instanceof Literal literal) {
            return literal.datatypeIRI();
        }

        return null;
    }
}
