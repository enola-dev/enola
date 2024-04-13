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
package dev.enola.thing.message;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.common.convert.ConversionException;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.LangString;
import dev.enola.thing.Link;
import dev.enola.thing.proto.Value.KindCase;
import dev.enola.thing.spi.AbstractThing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * ThingAdapter adapts a {@link dev.enola.thing.proto.Thing} to a {@link dev.enola.thing.Thing}.
 *
 * <p>This is somewhat similar to {@link ProtoThingIntoJavaThingBuilderConverter}, but this one only
 * "wraps" whereas that one "converts".
 */
@ThreadSafe
public final class ThingAdapter extends AbstractThing {

    // TODO This is too similar to ProtoThingIntoJavaThingBuilderConverter, and must be merged

    private static final Logger LOG = LoggerFactory.getLogger(ThingAdapter.class);

    private final dev.enola.thing.proto.Thing proto;
    private final DatatypeRepository datatypeRepository;

    public ThingAdapter(dev.enola.thing.proto.Thing proto, DatatypeRepository datatypeRepository) {
        this.proto = proto;
        this.datatypeRepository = datatypeRepository;
    }

    @Override
    public String iri() {
        return proto.getIri();
    }

    @Override
    public Collection<String> predicateIRIs() {
        return proto.getFieldsMap().keySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String predicateIRI) {
        var value = proto.getFieldsMap().get(predicateIRI);
        return (T) value(value);
    }

    @Override
    public String datatype(String predicateIRI) {
        var value = proto.getFieldsMap().get(predicateIRI);
        if (KindCase.LITERAL.equals(value.getKindCase())) return value.getLiteral().getDatatype();
        else return null;
    }

    private Object value(dev.enola.thing.proto.Value value) {
        return switch (value.getKindCase()) {
            case STRING -> value.getString();
            case LITERAL -> literal(value.getLiteral());
            case LANG_STRING -> langString(value.getLangString());
            case LINK -> new Link(value.getLink());
            case LIST -> list(value.getList());
            case STRUCT -> map(value.getStruct());
            case KIND_NOT_SET -> null;
        };
    }

    private dev.enola.thing.LangString langString(dev.enola.thing.proto.Value.LangString proto) {
        return new LangString(proto.getText(), proto.getLang());
    }

    private Object literal(dev.enola.thing.proto.Value.Literal literal) {
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

    private java.util.List<?> list(dev.enola.thing.proto.Value.List list) {
        // TODO Make this lazier... only convert object when they're actually used
        var protoValues = list.getValuesList();
        var listBuilder = ImmutableList.builderWithExpectedSize(protoValues.size());
        for (var value : protoValues) {
            listBuilder.add(value(value));
        }
        return listBuilder.build();
    }

    private java.util.Map<String, ?> map(dev.enola.thing.proto.Thing struct) {
        // TODO Make this lazier... only convert object when they're actually used
        var protoProperties = struct.getFieldsMap();
        var mapBuilder =
                ImmutableMap.<String, Object>builderWithExpectedSize(protoProperties.size());
        for (var entry : protoProperties.entrySet()) {
            mapBuilder.put(entry.getKey(), value(entry.getValue()));
        }
        return mapBuilder.build();
    }
}
