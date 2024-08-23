/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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
package dev.enola.web;

import com.google.common.util.concurrent.ListenableFuture;

import dev.enola.common.io.resource.ReadableResource;

import java.net.URI;

/**
 * Handler for Web Request.
 *
 * @see WebServer
 */
public interface WebHandler {

    // TODO Rename WebHandler to dev.enola.common.net.http.HttpHandler

    // TODO String instead of URI is better here, because more performant; URI has no use
    ListenableFuture<ReadableResource> get(URI uri);
}
