/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.mediatype;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.resource.AbstractResource;
import dev.enola.common.io.resource.Resource;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Detects the Charset of a Resource.
 *
 * <p>This interface is typically not used directly by {@link Resource} API users (who would just
 * use {@link MediaType#charset()} on an {@link AbstractResource#mediaType()}). Instead, it is
 * normally implemented by (some) <code>*MediaType</code> API implementations, for Charset detection
 * that is specific to a given MediaType (if any). For example, RFC 4627 §3 specifies how to
 * determine the encoding of JSON, or https://yaml.org/spec §5.2. specifies ditto for YAML.
 */
public interface ResourceCharsetDetector {

    /**
     * Detect the {@link Charset} by "sniffing" the source e.g. for "<a
     * href="https://en.wikipedia.org/wiki/Byte_order_mark">BOM detection</a>". The URI argument is
     * only uses for error messages. (It's never "accessed".)
     */
    Optional<Charset> detectCharset(URI uri, ByteSource source);
}
