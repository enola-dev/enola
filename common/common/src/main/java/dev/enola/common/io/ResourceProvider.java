package dev.enola.common.io;

import java.net.URI;

public interface ResourceProvider {

    ReadableResource getReadableResource(URI uri);

    WritableResource getWritableResource(URI uri);

    Resource getResource(URI uri);

}
