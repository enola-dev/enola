package dev.enola.tools.format;

import com.google.googlejavaformat.java.GoogleJavaFormatToolProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GoogleJavaFormatTest {
    @Test
    public void testGoogleJavaFormat() {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, UTF_8));
        PrintWriter err = new PrintWriter(new OutputStreamWriter(System.err, UTF_8));
        Assert.assertEquals(0, new GoogleJavaFormatToolProvider().run(out, err, "--help"));
    }
}
