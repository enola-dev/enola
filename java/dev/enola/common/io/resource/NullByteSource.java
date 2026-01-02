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
package dev.enola.common.io.resource;

import com.google.common.io.ByteSource;

import java.io.IOException;
import java.io.InputStream;

final class NullByteSource extends ByteSource {
    public static final ByteSource INSTANCE = new NullByteSource();

    private NullByteSource() {}

    @Override
    public InputStream openStream() throws IOException {
        return new NullInputStream();
    }

    private static final class NullInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            return 0;
        }
    }
}
