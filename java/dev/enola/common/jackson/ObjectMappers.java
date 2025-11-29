/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ObjectMappers {

    /**
     * A shared, thread-safe, "immutable" default {@link ObjectMapper} instance.
     *
     * <p>This instance MUST NOT be reconfigured (e.g. by calling {@link
     * ObjectMapper#configure(com.fasterxml.jackson.databind.DeserializationFeature, boolean)} or
     * similar methods) because it is shared.
     *
     * <p>If you need a specific configuration, use {@link #newObjectMapper()} to obtain a separate
     * new instance, configure it, and then keep it for re-use.
     */
    public static final ObjectMapper INSTANCE = newObjectMapper();

    /**
     * Creates a new {@link ObjectMapper} pre-configured with Enola defaults.
     *
     * <p>The returned instance is a new separate object which can be safely re-configured (e.g. to
     * set {@link com.fasterxml.jackson.databind.DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}
     * to false).
     *
     * <p>It is recommended to keep and re-use the obtained instance for performance reasons.
     */
    public static ObjectMapper newObjectMapper() {
        var objectMapper = new ObjectMapper();
        configure(objectMapper);
        return objectMapper;
    }

    /** Configure the given ObjectMapper with Enola-specific settings. */
    public static void configure(ObjectMapper mapper) {
        // Do NOT use mapper.findAndRegisterModules();
        // because that would mean that mapping would depend on (uncontrollable) classpath

        // https://github.com/FasterXML/jackson-modules-java8
        mapper.registerModule(new JavaTimeModule());
        // TODO Optional<T> support with mapper.registerModule(new Jdk8Module());

        SimpleModule module = new SimpleModule();
        module.addSerializer(Locale.class, new LocaleSerializer());
        module.addDeserializer(Locale.class, new LocaleDeserializer());
        mapper.registerModule(module);

        // Enable JSONc features: Java-style comments and trailing commas
        // https://github.com/enola-dev/enola/issues/1847
        mapper.getFactory().enable(JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature());
        mapper.getFactory().enable(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature());

        // DO fail on unknown properties - this helps to spot errors in configuration files etc.
        // NO! mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Always skip empty sequences ([]) and maps
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Always set missing sequences to empty collections
        var nullAsEmpty = JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY);
        mapper.configOverride(List.class).setSetterInfo(nullAsEmpty);
        mapper.configOverride(Set.class).setSetterInfo(nullAsEmpty);
        mapper.configOverride(Map.class).setSetterInfo(nullAsEmpty);

        // Always allow coercion for e.g. empty Map keys etc.
        mapper.coercionConfigFor(LogicalType.POJO)
                .setAcceptBlankAsEmpty(true)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
        mapper.coercionConfigFor(LogicalType.Array)
                .setAcceptBlankAsEmpty(true)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
        mapper.coercionConfigFor(LogicalType.Map)
                .setAcceptBlankAsEmpty(true)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
    }

    private ObjectMappers() {}
}
