package dev.enola.chat.jline;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.chat.IO;
import dev.enola.chat.SystemStdinStdoutTester;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class JLineIOTest /* TODO extends AbstractIOTester */ {

    @Test
    @Ignore
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
    @Ignore // TODO
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

    // TODO @Override
    protected IO io() throws IOException {
        return new JLineIO();
    }
}
