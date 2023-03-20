package dev.enola.common.io.resource;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class ErrorResource implements Resource {
    public static final ErrorResource INSTANCE = new ErrorResource();

    public static final String SCHEME = "error";

    private static final URI ERROR_URI = URI.create("error:-");

    private static final MediaType MEDIA_TYPE = MediaType.OCTET_STREAM;

    private ErrorResource() {}

    @Override
    public URI uri() {
        return ERROR_URI;
    }

    @Override
    public MediaType mediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public ByteSink byteSink() {
        return ErrorByteSink.INSTANCE;
    }

    @Override
    public ByteSource byteSource() {
        return ErrorByteSource.INSTANCE;
    }

    private static final class ErrorByteSink extends ByteSink {
        public static final ByteSink INSTANCE = new ErrorByteSink();

        private ErrorByteSink() {}
        @Override
        public OutputStream openStream() throws IOException {
            return new ErrorOutputStream();
        }
    }

    private static final class ErrorOutputStream extends OutputStream {
        @Override
        public void write(int i) throws IOException {
            throw new IOException();
        }
    }

    private static final class ErrorByteSource extends ByteSource {
        public static final ByteSource INSTANCE = new ErrorByteSource();

        @Override
        public InputStream openStream() throws IOException {
            throw new IOException();
        }
    }

    private static final class ErrorInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            throw new IOException();
        }
    }
}
