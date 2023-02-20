package dev.enola.core;

import dev.enola.core.proto.URI;
import org.junit.Assert;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

public class URIsTest {
    @Test
    public void testURI() {
        // Bad
        assertThrows(IllegalArgumentException.class, () -> URIs.from("|"));
        assertThrows(IllegalArgumentException.class, () -> URIs.from("demo"));
        assertThrows(IllegalArgumentException.class, () -> URIs.from("demo:"));

        // Good
        assertThat(URIs.from("demo:foo")).isEqualTo(URI.newBuilder().setParts(URI.Parts.newBuilder().setScheme("demo").setEntity("foo")).build());
    }
}