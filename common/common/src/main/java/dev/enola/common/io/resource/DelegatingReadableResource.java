package dev.enola.common.io.resource;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.net.MediaType;

import java.net.URI;

public abstract class DelegatingReadableResource implements ReadableResource {

    protected ReadableResource delegate;

    protected DelegatingReadableResource(ReadableResource delegate) {
        this.delegate = delegate;
    }

    @Override
    public URI uri() {
        return delegate.uri();
    }

    @Override
    public MediaType mediaType() {
        return delegate.mediaType();
    }

    @Override
    public ByteSource byteSource() {
        return delegate.byteSource();
    }

    @Override
    public CharSource charSource() {
        return delegate.charSource();
    }
}
