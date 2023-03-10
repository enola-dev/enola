package dev.enola.tools.proto;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

// TODO(vorburger) Upstream this!
// TODO Package this into a Bazel test rule?
public class TextProtoValidator {

    private final ExtensionRegistry extensionRegistry = ExtensionRegistry.getEmptyRegistry();

    // TODO scan for proto-file and proto-message headers
    // TODO support proto-import?

    public MessageOrBuilder validate(URL url, Message.Builder builder) {
        try {
            try (InputStream is = Resources.asByteSource(url).openStream()) {
                try (InputStreamReader readable = new InputStreamReader(is, Charsets.UTF_8)) {
                    TextFormat.getParser().merge(readable, extensionRegistry, builder);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("IOException while reading: " + url, e);
        }
        return null;
    }
}
