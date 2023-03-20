package dev.enola.common.io.resource;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class StringResourceTest {
    @Test
    public void testStringResource() throws IOException, URISyntaxException {
        var r = new StringResource("hello, world");
        assertThat(r.charSource().read()).isEqualTo("hello, world");

        assertThat(new ResourceProviders().getReadableResource(r.uri()).charSource().read())
                .isEqualTo("hello, world");

        assertThat(new StringResource("").byteSource().size()).isEqualTo(0);
        assertThat(new StringResource("").charSource().length()).isEqualTo(0);
    }
}
