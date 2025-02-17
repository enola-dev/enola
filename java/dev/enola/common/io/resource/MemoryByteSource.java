/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
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

import com.google.common.io.ByteSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

// Intentionally package local (for now)
class MemoryByteSource extends ByteSource {

    private final ByteArrayInputStream bais;

    public MemoryByteSource(byte[] bytes) {
        this.bais = new ByteArrayInputStream(bytes);
    }

    @Override
    public InputStream openStream() throws IOException {
        return bais;
    }
}
