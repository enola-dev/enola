package dev.enola.core;

import dev.enola.core.proto.ID;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

public class IDsTest {
    @Test
    public void testID() {
        // Good
        assertThat(IDs.from("demo:foo")).isEqualTo(ID.newBuilder().setParts(ID.Parts.newBuilder().setScheme("demo").setEntity("foo")).build());
        // TODO Use https://truth.dev/protobufs.html to assert object
        assertThat(IDs.from("k8s:pod?network=prod&context=demo&namespace=test&name=hello")).isNotNull();

        // Bad
        badEnolaID("|");                 // Illegal Character
        badEnolaID("demo:fo|o");         // Still illegal Character
        badEnolaID("demo");              // Path required
        badEnolaID("demo:");             // Still Path required
        badEnolaID("demo://foo");        // No "authority"
        badEnolaID("demo://foo/path");   // Still no "authority"
        badEnolaID("demo:foo#fragment"); // No Fragments (?)
    }

    private void badEnolaID(String id) {
        assertThrows(IllegalArgumentException.class, () -> IDs.from(id));
    }
}