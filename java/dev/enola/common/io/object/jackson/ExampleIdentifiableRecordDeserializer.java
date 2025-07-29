package dev.enola.common.io.object.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import dev.enola.common.io.object.Identifiable;
import dev.enola.common.io.object.ProviderFromID;

import java.io.IOException;

public class ExampleIdentifiableRecordDeserializer extends IdentifiableDeserializer {

    public ExampleIdentifiableRecordDeserializer(ProviderFromID provider) {
        super(provider);
    }

    @Override
    public Identifiable deserialize(JsonParser p, DeserializationContext ctx) throws IOException {}
}
