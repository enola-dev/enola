package dev.enola.common.io.object.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;
import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;

public class JsonObjectReaderWriter extends JacksonObjectReaderWriter {

    private static ObjectMapper newObjectMapper() {
        return new ObjectMapper();
    }

    public JsonObjectReaderWriter() {
        super(newObjectMapper());
    }

    @Override
    boolean canHandle(MediaType mediaType) {
        return MediaTypes.normalizedNoParamsEquals(mediaType, MediaType.JSON_UTF_8);
    }
}
