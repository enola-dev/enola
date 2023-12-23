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
package dev.enola.common.io.resource;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class FileDescriptorResourceTest {
    @Test
    public void testSTDOUT() throws IOException {
        var FD1 = new FileDescriptorResource(URI.create("fd:1?charset=UTF-8"));
        FD1.byteSink().write(new byte[] {1, 2, 3});
        FD1.charSink().write("hello");
    }
}
