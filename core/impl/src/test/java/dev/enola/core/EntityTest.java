package dev.enola.core;

import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.meta.proto.EntityKinds;
import dev.enola.core.proto.Entity;
import org.junit.Test;

import java.io.IOException;

public class EntityTest {
    @Test
    public void testReadingModels() throws IOException {
        ProtoIO.check("demo-model.textproto", EntityKinds.newBuilder());
        // TODO ProtoIO.check("demo-model.yaml", MetaModel.newBuilder());

        ProtoIO.check("foo-abc.textproto", Entity.newBuilder());
        ProtoIO.check("bar-abc-def.textproto", Entity.newBuilder());
    }

    // TODO testConvertingModels() throws IOException {
    // Transform demo-model.textproto to demo-model.yaml, make sure they match (and vice versa)
}
