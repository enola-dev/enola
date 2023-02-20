package dev.enola.core;

import dev.enola.core.proto.ID;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

public class IDsTest {

    String uri1 = "demo:foo";
    ID proto1 = ID.newBuilder().setParts(ID.Parts.newBuilder().setScheme("demo").setEntity("foo")).build();
    ID proto1text = ID.newBuilder().setText(uri1).build();

    String uri2 = "k8s:pod?network=prod&context=demo&namespace=test&name=hello";
    ID proto2 = ID.newBuilder().setParts(ID.Parts.newBuilder().setScheme("k8s").setEntity("pod")
            .addQuery(ID.NameValue.newBuilder().setName("network").setValue("prod"))
            .addQuery(ID.NameValue.newBuilder().setName("context").setValue("demo"))
            .addQuery(ID.NameValue.newBuilder().setName("namespace").setValue("test"))
            .addQuery(ID.NameValue.newBuilder().setName("name").setValue("hello"))
    ).build();

    @Test
    public void testParts() {
        assertThat(IDs.parts(proto1)).isEqualTo(proto1.getParts());
        assertThat(IDs.parts(proto1text)).isEqualTo(proto1.getParts());
    }

    @Test
    public void testToString() {
        assertThat(IDs.toString(proto1)).isEqualTo(uri1);
        assertThat(IDs.toString(proto1text)).isEqualTo(uri1);
        assertThat(IDs.toString(proto2)).isEqualTo(uri2);
    }

    @Test
    public void testURI() {
        // Good
        assertThat(IDs.from(uri1)).isEqualTo(proto1);
        assertThat(IDs.from(uri1 + "?")).isEqualTo(proto1);
        assertThat(IDs.from(uri2)).isEqualTo(proto2);

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