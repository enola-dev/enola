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
