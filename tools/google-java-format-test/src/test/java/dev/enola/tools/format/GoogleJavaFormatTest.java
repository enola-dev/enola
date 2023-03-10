package dev.enola.tools.format;

import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.google.googlejavaformat.java.Main;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GoogleJavaFormatTest {
    @Test
    public void testGoogleJavaFormat() {

        JavaFormatterOptions options =
                JavaFormatterOptions.builder()
                        .style(JavaFormatterOptions.Style.GOOGLE)
                        .formatJavadoc(true)
                        .reorderModifiers(true)
                        .build();

        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, UTF_8));
        PrintWriter err = new PrintWriter(new OutputStreamWriter(System.err, UTF_8));
        InputStream in = InputStream.nullInputStream();
        new Main(out, err, in).format("GoogleJavaFormatTest.java");
    }
}
