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
package dev.enola.common.exec.pty;

import java.io.IOException;
import java.io.OutputStream;

// TODO If kept, then later move this to dev.enola.common.io
class TeeOutputStream extends OutputStream {
    private final OutputStream stream1;
    private final OutputStream stream2;

    public TeeOutputStream(OutputStream stream1, OutputStream stream2) {
        this.stream1 = stream1;
        this.stream2 = stream2;
    }

    @Override
    public void write(int b) throws IOException {
        stream1.write(b);
        stream2.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        stream1.write(b);
        stream2.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        stream1.write(b, off, len);
        stream2.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        stream1.flush();
        stream2.flush();
    }

    // NOT close() - it's not up to use to close either stream1 or stream2 here!
}
