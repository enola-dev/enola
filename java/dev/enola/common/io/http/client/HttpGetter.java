/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.http.client;

import com.google.common.net.MediaType;

import java.util.stream.Stream;

/** Interface for HTTP GET operations returning MediaType and streaming response lines. */
@FunctionalInterface
public interface HttpGetter {

    /** HTTP GET response containing media type and streaming lines. */
    record Response(MediaType mediaType, Stream<String> lines) implements AutoCloseable {
        @Override
        public void close() {
            if (lines != null) {
                lines.close();
            }
        }
    }

    /**
     * Performs an HTTP GET request and returns the response resource with streaming lines.
     *
     * @param url the URL or path to request
     * @return the response containing media type and a stream of lines
     */
    Response getLines(String url);
}
