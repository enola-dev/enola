package dev.enola.common.io.resource;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class NullResource implements Resource {

    public static final NullResource INSTANCE = new NullResource();

    public static final String SCHEME = "null";

    private static final URI NULL_URI = URI.create("null:-");
    private static final MediaType MEDIA_TYPE = MediaType.OCTET_STREAM;

    private NullResource() {}

    @Override
    public URI uri() {
        return NULL_URI;
    }

    @Override
    public MediaType mediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public ByteSink byteSink() {
        return NullByteSink.INSTANCE;
    }

    @Override
    public ByteSource byteSource() {
        return NullByteSource.INSTANCE;
    }

    // TODO https://github.com/google/guava/issues/2011
    private static final class NullByteSink extends ByteSink {
        public static final ByteSink INSTANCE = new NullByteSink();

        private NullByteSink() {}

        @Override
        public OutputStream openStream() throws IOException {
            return ByteStreams.nullOutputStream();
        }
    }

    private static final class NullByteSource extends ByteSource {
        public static final ByteSource INSTANCE = new NullByteSource();

        private NullByteSource() {}

        @Override
        public InputStream openStream() throws IOException {
            return new NullInputStream();
        }
    }

    private static final class NullInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            return 0;
        }
    }
}
