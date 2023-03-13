package dev.enola.common.io;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import com.google.common.net.MediaType;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public class FileResource implements Resource {

    private final Path path;
    private final OpenOption[] openOptions;

    public FileResource(Path path, OpenOption... openOptions) {
        this.path = path;
        this.openOptions = openOptions;
    }

    @Override
    public URI uri() {
        return path.toUri();
    }

    @Override
    public MediaType mediaType() {
        try {
            return MediaType.parse(Files.probeContentType(path));
        } catch (IOException e) {
            return MediaType.ANY_TYPE;
        }
    }

    @Override
    public ByteSink byteSink() {
        return MoreFiles.asByteSink(path, openOptions);
    }

    @Override
    public ByteSource byteSource() {
        return MoreFiles.asByteSource(path, openOptions);
    }
}
