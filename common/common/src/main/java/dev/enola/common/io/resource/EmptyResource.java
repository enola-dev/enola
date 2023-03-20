package dev.enola.common.io.resource;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.net.MediaType;
import java.net.URI;

public class EmptyResource implements ReadableResource {
    public static final String SCHEME = "empty";

    private static final URI EMPTY_URI = URI.create("empty:-");

    private static final MediaType MEDIA_TYPE = MediaType.OCTET_STREAM;

    @Override
    public URI uri() {
        return EMPTY_URI;
    }

    @Override
    public MediaType mediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public ByteSource byteSource() {
        return ByteSource.empty();
    }

    @Override
    public CharSource charSource() {
        return CharSource.empty();
    }
}
