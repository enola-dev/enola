package dev.enola.common.io;

import com.google.common.io.ByteSink;
import com.google.common.io.CharSink;
import com.google.common.net.MediaType;

import java.net.URI;
import java.nio.charset.Charset;

public interface WritableResource {

    URI uri();

    MediaType mediaType();

    ByteSink byteSink();

    default CharSink charSink(Charset fallback) {
        return byteSink().asCharSink(mediaType().charset().or(fallback));
    }
}
