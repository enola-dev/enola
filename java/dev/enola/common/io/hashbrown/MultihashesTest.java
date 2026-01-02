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
package dev.enola.common.io.hashbrown;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.io.ByteSource;

import io.ipfs.multihash.Multihash;

import org.junit.Test;

import java.io.IOException;

public class MultihashesTest {

    // See also dev.enola.common.io.resource.MemoryByteSourceTest

    @Test
    public void hashEmpty() throws IOException {
        var emptyByteSource = ByteSource.empty();
        var multihash1 = Multihashes.hash(emptyByteSource, Multihash.Type.sha2_512);
        var multihash2 = Multihashes.hash(emptyByteSource, Multihash.Type.sha2_512);
        assertThat(multihash2).isEqualTo(multihash1);
    }

    @Test
    public void hashWrap() throws IOException {
        var hello1 = ByteSource.wrap("hello, world".getBytes());
        var multihash1 = Multihashes.hash(hello1, Multihash.Type.sha2_512);

        var hello2 = ByteSource.wrap("hello, world".getBytes());
        var multihash2 = Multihashes.hash(hello2, Multihash.Type.sha2_512);

        assertThat(multihash2).isEqualTo(multihash1);
    }
}
