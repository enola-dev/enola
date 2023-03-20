package dev.enola.common.io.resource;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.net.MediaType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class StringResource implements ReadableResource {

    public static final String SCHEME = "string";

    private static final MediaType MEDIA_TYPE = MediaType.PLAIN_TEXT_UTF_8;

    private final String string;
    private final URI uri;

    public StringResource(String s) {
        this.string = Objects.requireNonNull(s);
        try {
            if (!s.isEmpty()) {
                this.uri = new URI(SCHEME, string, null);
            }else {
                this.uri = new EmptyResource().uri();
            }

        } catch (URISyntaxException e) {
            // This should never happen, if the escaping above is correct...
            throw new IllegalArgumentException("String is invalid in URI: " + s, e);
        }
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public MediaType mediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public ByteSource byteSource() {
        return charSource().asByteSource(MEDIA_TYPE.charset().get());
    }

    @Override
    public CharSource charSource() {
        return CharSource.wrap(string);
    }
}
