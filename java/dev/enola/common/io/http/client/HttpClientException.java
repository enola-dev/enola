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

/** Thrown when the server returns an unexpected HTTP response or on network errors. */
public final class HttpClientException extends RuntimeException {
    private final int statusCode;
    private final String body;

    public HttpClientException(int actualStatusCode, String body, int expectedStatusCode) {
        super(
                "Server returned HTTP "
                        + actualStatusCode
                        + ", but expected "
                        + expectedStatusCode
                        + ": "
                        + body);
        this.statusCode = actualStatusCode;
        this.body = body;
    }

    public HttpClientException(Throwable cause) {
        super(cause);
        this.statusCode = 0;
        this.body = "";
    }

    public HttpClientException(String message) {
        super(message);
        this.statusCode = 0;
        this.body = "";
    }

    public int statusCode() {
        return statusCode;
    }

    public String body() {
        return body;
    }
}
