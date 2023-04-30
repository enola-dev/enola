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
package dev.enola.web;

import java.net.InetSocketAddress;

/**
 * Web Server API. Intended to be use by both HTML+JS UI and REST API servers. Implementations for
 * this API could be based on e.g. the <tt>com.sun.net.httpserver.HttpServer</tt>, or <i>Netty</i>,
 * or <i>Jetty</i> or <i>Tomcat</i> or <i>Vert.x</i> - or any other similar such HTTP framework.
 * Please note that there may well also be non-open source implementations which map this API to
 * some proprietary in-house web frameworks.
 */
public interface WebServer extends AutoCloseable {

    void register(String path, WebHandler h);

    void start();

    InetSocketAddress getInetAddress();

    void close();
}
