package dev.enola.be;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class BeTest {
    @Test
    public void testBe() throws IOException {
        Be.main(new String[] { "--", "echo", "hello, ", "world", ">hi.txt" });
        assertThat(Files.readString(Path.of("hi.txt"))).isEqualTo("hello, world");
    }
}
