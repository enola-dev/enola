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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import dev.enola.common.io.object.Identifiable;
import dev.enola.common.io.object.ProviderFromID;

import org.jspecify.annotations.Nullable;

import java.io.IOException;

/**
 * Custom Jackson Deserializer for {@link Identifiable} objects. This deserializer expects to
 * receive a String (the ID) and uses a {@link ProviderFromID} (like {@link
 * dev.enola.common.io.object.ObjectStore}) to fetch the full object.
 *
 * <p>It implements {@link ContextualDeserializer} to determine the concrete type it needs to
 * deserialize into based on the field's type in the containing object.
 */
class IdentifiableDeserializer extends JsonDeserializer<Identifiable>
        implements ContextualDeserializer {

    private final ProviderFromID provider;

    // The actual concrete type this deserializer instance is responsible for.
    // This is set during createContextual().
    private final @Nullable Class<? extends Identifiable> handledType;

    /**
     * Constructor to allow injection of ProviderFromID. This constructor is used when registering
     * the deserializer with a SimpleModule or when createContextual creates a new instance.
     *
     * @param provider The ProviderFromID to use for fetching objects.
     */
    IdentifiableDeserializer(ProviderFromID provider) {
        this(provider, null); // Handled type will be set in createContextual
    }

    /**
     * Private constructor used by createContextual() to create a contextual instance.
     *
     * @param provider The ProviderFromID to use for fetching objects.
     * @param handledType The specific concrete Identifiable type to deserialize to.
     */
    private IdentifiableDeserializer(
            ProviderFromID provider, @Nullable Class<? extends Identifiable> handledType) {
        this.provider = provider;
        this.handledType = handledType;
    }

    /**
     * This is the core deserialization logic. It reads the ID string from the JSON and uses the
     * provider to get the object.
     *
     * @param p The JsonParser to read from.
     * @param ctx The DeserializationContext.
     * @return The deserialized Identifiable object.
     * @throws IOException If there's an issue reading from the parser.
     */
    @Override
    public @Nullable Identifiable deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        String id = p.getText(); // Read the string value (which is the ID)

        if (id == null || id.isEmpty()) {
            return null; // Handle null or empty IDs gracefully
        }

        // Ensure the deserializer has been properly contextualized
        // The provider should always be non-null if registered correctly.
        // handledType will be null for the initial instance, but non-null for contextualized ones.
        if (provider == null || handledType == null) {
            // This case should ideally not be reached if the setup is correct
            // but acts as a safeguard.
            throw new IllegalStateException(
                    "IdentifiableDeserializer not properly contextualized. Provider or handledType"
                        + " is missing. Ensure it's registered via SimpleModule with an injected"
                        + " ProviderFromID, and that the type is Identifiable.");
        }

        // Use the provider to get the actual Identifiable object based on ID and type
        return provider.get(id, handledType);
    }

    /**
     * This method is called by Jackson to create a contextual instance of the deserializer. It
     * allows the deserializer to adapt to the specific type it's handling.
     *
     * @param ctx The DeserializationContext.
     * @param property The BeanProperty being deserialized (e.g., the field in ExampleRecord).
     * @return A new, contextualized instance of IdentifiableDeserializer.
     * @throws JsonMappingException If there's an error in mapping.
     */
    @Override
    @SuppressWarnings("unchecked")
    public JsonDeserializer<?> createContextual(
            DeserializationContext ctx, @Nullable BeanProperty property)
            throws JsonMappingException {
        // Determine the actual type that this deserializer needs to produce.
        // For a field like 'ExampleIdentifiableRecord exampleIdentifiableRecord',
        // 'type' will be JavaType for ExampleIdentifiableRecord.
        JavaType type = (property != null) ? property.getType() : ctx.getContextualType();
        if (type.isContainerType()) {
            type = type.getContentType();
        }
        Class<?> rawClass = type.getRawClass();

        // If this instance is already contextualized for the target type, stop recursion.
        // This check is crucial to break an infinite loop causing java.lang.StackOverflowError.
        if (handledType != null && handledType.equals(rawClass)) {
            return this;
        }

        // Ensure the type is assignable from Identifiable. If not, let Jackson use its default.
        if (!type.isTypeOrSubTypeOf(Identifiable.class)) {
            // If the type is not Identifiable, let Jackson find a default deserializer
            // This can happen if the deserializer is registered globally but used on a
            // non-Identifiable type.
            return ctx.findContextualValueDeserializer(type, property);
        }

        // Create a new instance of the deserializer with the specific provider and handledType
        // We cast type.getRawClass() to Class<? extends Identifiable> because we've checked
        // isAssignableFrom.
        return new IdentifiableDeserializer(provider, (Class<? extends Identifiable>) rawClass);
    }
}
