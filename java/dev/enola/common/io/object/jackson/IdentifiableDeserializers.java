/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.object.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;

import dev.enola.common.io.object.Identifiable;
import dev.enola.common.io.object.ProviderFromID;

/**
 * A custom deserializers factory that provides {@link IdentifiableDeserializer} for any type that
 * implements the {@link Identifiable} interface. To avoid needing annotations, this is registered
 * with {@link com.fasterxml.jackson.databind.module.SimpleModule} in the {@link
 * JacksonObjectReaderWriter}.
 */
class IdentifiableDeserializers extends SimpleDeserializers {
    private final ProviderFromID provider;

    public IdentifiableDeserializers(ProviderFromID provider) {
        this.provider = provider;
    }

    @Override
    public JsonDeserializer<?> findBeanDeserializer(
            JavaType type, DeserializationConfig config, BeanDescription beanDesc)
            throws JsonMappingException {
        if (type.isTypeOrSubTypeOf(Identifiable.class)) {
            return new IdentifiableDeserializer(provider);
        }
        return super.findBeanDeserializer(type, config, beanDesc);
    }
}
