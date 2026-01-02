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

import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import dev.enola.common.convert.ConversionException;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.LangString;
import dev.enola.thing.Link;
import dev.enola.thing.PredicatesObjects;
import dev.enola.thing.impl.IImmutablePredicatesObjects;
import dev.enola.thing.impl.ImmutablePredicatesObjects;
import dev.enola.thing.proto.Value;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PredicatesObjectsAdapter implements IImmutablePredicatesObjects {

    // TODO This is too similar to ProtoThingIntoJavaThingBuilderConverter, and must be merged

    private static final Logger LOG = LoggerFactory.getLogger(ThingAdapter.class);

    protected final dev.enola.thing.proto.Thing proto;

    @SuppressWarnings("Immutable")
    protected final DatatypeRepository datatypeRepository;

    public PredicatesObjectsAdapter(
            dev.enola.thing.proto.Thing proto, DatatypeRepository datatypeRepository) {
        this.proto = requireNonNull(proto, "proto");
        // TODO requireNonNull(datatypeRepository, "datatypeRepository");
        this.datatypeRepository = datatypeRepository;
    }

    @Override
    public ImmutableSet<String> predicateIRIs() {
        return ImmutableSet.copyOf(proto.getPropertiesMap().keySet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(String predicateIRI) {
        var value = proto.getPropertiesMap().get(predicateIRI);
        if (value != null) return (T) value(value);
        else return null;
    }

    @Override
    public ImmutableMap<String, Object> properties() {
        var predicateIRIs = predicateIRIs();
        var builder = ImmutableMap.<String, Object>builderWithExpectedSize(predicateIRIs.size());
        for (var predicateIRI : predicateIRIs) {
            var value = get(predicateIRI);
            if (value != null) builder.put(predicateIRI, value);
        }
        return builder.build();
    }

    @Override
    public ImmutableMap<String, String> datatypes() {
        var predicateIRIs = predicateIRIs();
        var builder = ImmutableMap.<String, String>builderWithExpectedSize(predicateIRIs.size());
        for (var predicateIRI : predicateIRIs) {
            var datatype = datatype(predicateIRI);
            if (datatype != null) builder.put(predicateIRI, datatype);
        }
        return builder.build();
    }

    @Override
    public @Nullable String datatype(String predicateIRI) {
        var value = proto.getPropertiesMap().get(predicateIRI);
        if (Value.KindCase.LITERAL.equals(value.getKindCase()))
            return value.getLiteral().getDatatype();
        else return null;
    }

    private @Nullable Object value(dev.enola.thing.proto.Value value) {
        return switch (value.getKindCase()) {
            case STRING -> value.getString();
            case LITERAL -> literal(value.getLiteral());
            case LANG_STRING -> langString(value.getLangString());
            case LINK -> new Link(value.getLink());
            case LIST -> listOrSet(value.getList());
            case STRUCT -> map(value.getStruct());
            case KIND_NOT_SET -> null;
        };
    }

    private dev.enola.thing.LangString langString(dev.enola.thing.proto.Value.LangString proto) {
        return new LangString(proto.getText(), proto.getLang());
    }

    private @Nullable Object literal(dev.enola.thing.proto.Value.Literal literal) {
        var literalValue = literal.getValue();
        var datatypeIRI = literal.getDatatype();
        var datatype = datatypeRepository.get(datatypeIRI);
        if (datatype == null) return new dev.enola.thing.Literal(literalValue, datatypeIRI);
        try {
            return datatype.stringConverter().convertFrom(literalValue);
        } catch (ConversionException e) {
            LOG.warn("Failed to convert '{}'' of datatype {}", literalValue, datatypeIRI, e);
            return new dev.enola.thing.Literal(literalValue, datatypeIRI);
        }
    }

    private ImmutableCollection<?> listOrSet(dev.enola.thing.proto.Value.List list) {
        // TODO Make this lazier... only convert object when they're actually used
        var protoValues = list.getValuesList();
        ImmutableCollection.Builder<Object> collectionBuilder;
        if (list.getOrdered()) {
            collectionBuilder = ImmutableList.builderWithExpectedSize(protoValues.size());
        } else {
            collectionBuilder = ImmutableSet.builderWithExpectedSize(protoValues.size());
        }

        for (var protoValue : protoValues) {
            var value = value(protoValue);
            if (value != null) collectionBuilder.add(value);
        }
        return collectionBuilder.build();
    }

    private PredicatesObjectsAdapter map(dev.enola.thing.proto.Thing struct) {
        return new PredicatesObjectsAdapter(struct, datatypeRepository);
    }

    @Override
    public PredicatesObjects.Builder<? extends PredicatesObjects> copy() {
        // TODO Alternatively to this approach, we could also wrap a Proto Thing Builder
        var properties = properties();
        var builder = ImmutablePredicatesObjects.builderWithExpectedSize(properties.size());
        properties.forEach(
                (predicate, value) -> set(builder, predicate, value, datatype(predicate)));
        return builder;
    }

    @SuppressWarnings("Immutable") // TODO Object value https://errorprone.info/bugpattern/Immutable
    private void set(
            Builder<? extends IImmutablePredicatesObjects> builder,
            String predicate,
            Object value,
            @Nullable String datatype) {
        builder.set(predicate, value, datatype);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        // NO NEED: if (obj == null) return false;
        // NOT:     if (getClass() != obj.getClass()) return false;
        if (!(obj instanceof PredicatesObjectsAdapter other)) return false;
        if (obj instanceof ThingAdapter) return false; // skipcq: JAVA-W0095
        // The skipcq works around an apparent SpotBugs error; because ThingAdapter *IS* a subtype?!
        // https://spotbugs.readthedocs.io/en/latest/bugDescriptions.html#eq-equals-checks-for-incompatible-operand-eq-check-for-operand-not-compatible-with-this
        return this.proto.equals(other.proto)
                && this.datatypeRepository.equals(other.datatypeRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proto, datatypeRepository);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("properties", properties()).toString();
    }
}
