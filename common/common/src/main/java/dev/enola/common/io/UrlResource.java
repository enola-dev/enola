package dev.enola.common.io;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;

public class UrlResource implements ReadableResource {

    private final URL url;
    private final URI uri;

    public UrlResource(URL url) {
        this.url = url;
        try {
            this.uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public MediaType mediaType() {
        // This is slow - but more accurate; see https://www.baeldung.com/java-file-mime-type
        URLConnection c = null;
        try {
            c = url.openConnection();
            var type = c.getContentType();
            var enc = c.getContentEncoding();
            return MediaType.parse(type).withCharset(Charset.forName(enc));
        } catch (IOException e) {
            return MediaType.ANY_TYPE;
        } finally {
            ((HttpURLConnection) c).disconnect();
        }
    }

    @Override
    public ByteSource byteSource() {
        return Resources.asByteSource(url);
    }
}
