package dev.enola.common.io;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.net.MediaType;

import java.net.URI;
import java.nio.charset.Charset;

public interface ReadableResource {

    URI uri();

    MediaType mediaType();

    ByteSource byteSource();

    default CharSource charSource(Charset fallback) {
        return byteSource().asCharSource(mediaType().charset().or(fallback));
    }

    // NO contentLength() because ByteSource already has a size() + sizeIfKnown()
}
