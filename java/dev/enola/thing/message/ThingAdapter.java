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
package dev.enola.thing.message;

import com.google.common.base.MoreObjects;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;

/**
 * ThingAdapter adapts a {@link dev.enola.thing.proto.Thing} to a {@link dev.enola.thing.Thing}.
 *
 * <p>This is somewhat similar to {@link ProtoThingIntoJavaThingBuilderConverter}, but this one only
 * "wraps" whereas that one "converts".
 */
@ThreadSafe
public final class ThingAdapter extends PredicatesObjectsAdapter implements Thing {

    public ThingAdapter(dev.enola.thing.proto.Thing proto) {
        this(proto, DatatypeRepository.CTX);
    }

    public ThingAdapter(dev.enola.thing.proto.Thing proto, DatatypeRepository datatypeRepository) {
        super(proto, datatypeRepository);
    }

    @Override
    public String iri() {
        return proto.getIri();
    }

    @Override
    @SuppressWarnings("Immutable") // TODO Remove when switching to (TBD) PredicatesObjects.Visitor
    public Thing.Builder<? extends Thing> copy() {
        // TODO Alternatively to this approach, we could also wrap a Proto Thing Builder
        var properties = properties();
        var builder = ImmutableThing.builderWithExpectedSize(properties.size());
        builder.iri(iri());
        properties.forEach(
                (predicate, value) -> builder.set(predicate, value, datatype(predicate)));
        return builder;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        // NO NEED: if (obj == null) return false;
        // NOT:     if (getClass() != obj.getClass()) return false;
        if (!(obj instanceof ThingAdapter other)) return false;
        return this.proto.equals(other.proto)
                && this.datatypeRepository.equals(other.datatypeRepository);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("iri", iri())
                .add("properties", properties())
                .toString();
    }
}
