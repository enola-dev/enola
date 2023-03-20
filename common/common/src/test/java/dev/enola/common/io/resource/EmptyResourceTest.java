package dev.enola.common.io.resource;

import com.google.common.truth.Truth;import org.junit.Test;
import java.io.IOException;import static com.google.common.truth.Truth.assertThat;

public class EmptyResourceTest {
    @Test
    public void testEmptyResource()throws IOException {
        var e = new EmptyResource();
        assertThat(e.byteSource().isEmpty()).isTrue();
        assertThat(e.charSource().isEmpty()).isTrue();
        assertThat(e.mediaType()).isNotNull();
        assertThat(e.uri()).isNotNull();
    }
}
