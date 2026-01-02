/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.ai.adk.web;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.ai.adk.test.MockAgent;
import dev.enola.common.io.resource.OkHttpResource;

import org.junit.Test;

public class AdkHttpServerTest {

    // TODO Upstream (something like) this, because ADK has no test coverage for dev/

    @Test
    public void startStop() throws Exception {
        try (var server = AdkHttpServer.start(new MockAgent("hello, world"), 0)) {
            var tester = new OkHttpResource("http://localhost:" + server.httpPort());
            var html = tester.charSource().read();
            assertThat(html).contains("<title>Agent Development Kit Dev UI</title>");
        }
    }
}
