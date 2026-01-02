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
package dev.enola.thing;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.datatype.DatatypeRepository;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

// package-local
class ObjectConversions {

    @SuppressWarnings("unchecked")
    static <T> Optional<T> as(
            @Nullable Object object,
            Class<T> klass,
            PredicatesObjects predicatesObjects,
            String predicateIRI) {
        if (object == null) return Optional.empty();
        if (klass.isInstance(object)) return Optional.of((T) object);
        if (String.class.equals(klass)) {
            // TODO Remove! This actually seems like a Real Bad Idea(TM)(C)(R), in hindsight...
            switch (object) {
                case Literal literal:
                    return Optional.of((T) literal.value());
                case URI uri:
                    return Optional.of((T) uri.toString());
                case Link(String iri):
                    return Optional.of((T) iri);
                case Collection<?> collection:
                    // return Optional.of((T) Collections2.transform(collection, e ->
                    // e.toString()).toString());
                    throw new IllegalStateException(
                            predicateIRI + " is not a String, but: " + object);
                // TODO Ideally, it should look up the "right" text, using a Lang Ctx Key from TLC
                case LangString langString:
                    return Optional.of((T) langString.text());
                default:
                    break;
            }
        }
        if (klass.isAssignableFrom(Iterable.class) && !(object instanceof Iterable))
            return Optional.of((T) Collections.singleton(object));
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
