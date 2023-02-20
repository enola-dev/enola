package dev.enola.core;

import dev.enola.core.proto.ID;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

public class IDsTest {
    @Test
    public void testID() {
        // Good
        ID proto = ID.newBuilder().setParts(ID.Parts.newBuilder().setScheme("demo").setEntity("foo")).build();
        assertThat(IDs.from("demo:foo")).isEqualTo(proto);
        assertThat(IDs.from("demo:foo?")).isEqualTo(proto);

        assertThat(IDs.from("k8s:pod?network=prod&context=demo&namespace=test&name=hello")).isEqualTo(
            ID.newBuilder().setParts(ID.Parts.newBuilder().setScheme("k8s").setEntity("pod")
                .addQuery(ID.NameValue.newBuilder().setName("network").setValue("prod"))
                .addQuery(ID.NameValue.newBuilder().setName("context").setValue("demo"))
                .addQuery(ID.NameValue.newBuilder().setName("namespace").setValue("test"))
                .addQuery(ID.NameValue.newBuilder().setName("name").setValue("hello"))
            ).build());

        // Bad
        badEnolaID("|");                    // Illegal Character
        badEnolaID("demo:fo|o");            // Still illegal Character
        badEnolaID("demo");                 // Path required
        badEnolaID("demo:");                // Still Path required
        badEnolaID("demo://foo");           // No "authority"
        badEnolaID("demo://foo/path");      // Still no "authority"
        badEnolaID("demo:foo#fragment");    // No Fragments (?)
        badEnolaID("demo:foo?bad=a=b");     // Nope, this is confusing
        badEnolaID("demo:foo?bad=a&bad=b"); // Duplicate query names
    }

    private void badEnolaID(String id) {
        assertThrows(IllegalArgumentException.class, () -> IDs.from(id));
    }
}