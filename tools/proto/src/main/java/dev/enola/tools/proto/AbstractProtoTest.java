package dev.enola.tools.proto;

import org.junit.Test;

public abstract class AbstractProtoTest {
    private final String pathToResourceOnClasspath;

    public AbstractProtoTest(String pathToResourceOnClasspath) {
        this.pathToResourceOnClasspath = pathToResourceOnClasspath;
    }

    @Test public void testTextProtoValidation() {
        // TODO Implement, using TextProtoValidator!
    }
}
