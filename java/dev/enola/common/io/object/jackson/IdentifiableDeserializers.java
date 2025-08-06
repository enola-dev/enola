package dev.enola.common.io.object.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;

import dev.enola.common.io.object.Identifiable;
import dev.enola.common.io.object.ProviderFromID;

/**
 * A custom deserializers factory that provides IdentifiableDeserializer for any type that
 * implements the Identifiable interface. This is registered with SimpleModule to avoid needing
 * annotations.
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
        // IMPORTANT: Only return our custom deserializer if the requested type is
        // *exactly* the Identifiable interface.
        // If it's a concrete class that implements Identifiable (like ExampleIdentifiableRecord),
        // we return null, letting Jackson's default bean deserializer handle the introspection
        // of that concrete class. Our IdentifiableDeserializer will then be picked up
        // by createContextual for the *field* itself.
        if (type.isTypeOrSubTypeOf(Identifiable.class)) {
            return new IdentifiableDeserializer(provider);
        }

        // Otherwise, let Jackson's default deserializer factory handle it.
        return super.findBeanDeserializer(type, config, beanDesc);
    }
}
