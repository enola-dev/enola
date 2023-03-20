package dev.enola.core.meta;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.IDs;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.ID;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

public class EntityKindRepositoryTest {

    EntityKindRepository r = new EntityKindRepository();

    @Test public void testEmptyRepository() throws ValidationException {
        assertThat(r.list()).isEmpty();
        assertThat(r.listID()).isEmpty();

        var id = ID.newBuilder().setEntity("non-existant").build();
        assertThrows(IllegalArgumentException.class, () -> r.get(id));
    }

    @Test public void testBasics() throws ValidationException {
        var id = ID.newBuilder().setNs("testNS").setEntity("testEntity").build();
        var ek = EntityKind.newBuilder().setId(id).build();

        r.put(ek);
        assertThat(r.get(id)).isEqualTo(ek);
        assertThat(r.listID()).containsExactly(id);
        assertThat(r.list()).containsExactly(ek);
    }

    @Test public void testEmptyNamespace()throws ValidationException {
        var id = ID.newBuilder().setEntity("testEntity").build();
        var ek = EntityKind.newBuilder().setId(id).build();

        r.put(ek);
        assertThat(r.get(id)).isEqualTo(ek);
        assertThat(r.listID()).containsExactly(id);
        assertThat(r.list()).containsExactly(ek);
    }

    @Test public void testEmptyName() {
        var id = ID.newBuilder().build();
        var ek = EntityKind.newBuilder().setId(id).build();

        assertThrows(IllegalArgumentException.class, () -> r.get(id));
        assertThrows(ValidationException.class, () -> r.put(ek));
    }

    @Test public void testLoad() throws ValidationException, IOException {
        r.load(new ClasspathResource("demo-model.textproto"));
        assertThat(r.listID()).containsExactly(IDs.parse("demo.foo/name"), IDs.parse("demo.bar/foo/name"), IDs.parse("demo.baz/uuid"));
        assertThat(r.list()).hasSize(3);
    }
}
