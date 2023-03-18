/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.enola.common.protobuf;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.TextFormat;

import dev.enola.common.io.resource.ReadableResource;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

public class ProtoIO {

    // TODO Support print() to Printer
    // TODO Support .json and .binary formats!
    // TODO scan for proto-file and proto-message headers to support DynamicMessage
    // TODO support proto-import?

    private final ExtensionRegistry extensionRegistry = ExtensionRegistry.getEmptyRegistry();

    public <M extends Message> M merge(
            ReadableResource resource, Builder builder, Class<M> messageClass) throws IOException {
        return (M) merge(resource, builder).build();
    }

    public <B extends Builder> B merge(ReadableResource resource, B builder) throws IOException {
        try (Reader reader = resource.charSource().openBufferedStream()) {
            TextFormat.getParser().merge(reader, extensionRegistry, builder);
            return builder;
        } catch (TextFormat.ParseException e) {
            throw new TextParseException(resource.uri(), e);
        }
    }

    public static class TextParseException extends TextFormat.ParseException {

        private final URI uri;

        public TextParseException(URI uri, TextFormat.ParseException e) {
            super(e.getLine(), e.getColumn(), uri.toString() + ":" + e.getMessage());
            this.uri = uri;
        }
    }
}
