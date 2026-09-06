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
package dev.enola.common.io.http.client.jdk;

import com.google.common.net.MediaType;

import dev.enola.common.io.http.client.HttpClientException;
import dev.enola.common.io.http.client.HttpGetter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.stream.Stream;

public class HttpClient implements HttpGetter {

    private static final Duration TIMEOUT = Duration.ofSeconds(21);
    private final java.net.http.HttpClient jdkHttpClient;

    public HttpClient() {
        this.jdkHttpClient =
                java.net.http.HttpClient.newBuilder()
                        .connectTimeout(TIMEOUT)
                        .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                        .build();
    }

    @Override
    public HttpGetter.Response getLines(String url) {
        try {
            HttpRequest request =
                    HttpRequest.newBuilder().uri(URI.create(url)).timeout(TIMEOUT).GET().build();
            HttpResponse<Stream<String>> response =
                    jdkHttpClient.send(request, HttpResponse.BodyHandlers.ofLines());
            if (response.statusCode() != 200) {
                response.body().close();
                throw new HttpClientException("HTTP " + response.statusCode() + " for " + url);
            }
            MediaType mediaType =
                    response.headers()
                            .firstValue("Content-Type")
                            .map(MediaType::parse)
                            .orElse(MediaType.OCTET_STREAM);
            return new HttpGetter.Response(mediaType, response.body());
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException(e);
        }
    }
}
