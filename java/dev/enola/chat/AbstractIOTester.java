package dev.enola.chat;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.IOException;

public abstract class AbstractIOTester {

    protected abstract IO io() throws IOException;

    @Test
    public void stdin() throws IOException {
        var io = io();
        SystemStdinStdoutTester.pipeIn(
                "hello\nworld\nend", // Intentionally no last \n at the end!
                () -> {
                    assertThat(io.readLine()).isEqualTo("hello");
                    assertThat(io.readLine()).isEqualTo("world");
                    assertThat(io.readLine()).isEqualTo("end");
                });
    }

    @Test
    public void stdout() throws IOException {
        var io = io();
        var out =
                SystemStdinStdoutTester.captureOut(
                        () -> {
                            System.out.println("hello");
                            io.printf("world");
                        });
        assertThat(out).isEqualTo("hello\nworld");
    }
}
