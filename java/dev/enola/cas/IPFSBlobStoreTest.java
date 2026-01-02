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
package dev.enola.cas;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.io.ByteSource;

import dev.enola.common.Net;
import dev.enola.common.function.CheckedSupplier;

import io.ipfs.api.IPFS;
import io.ipfs.cid.Cid;

import org.jspecify.annotations.Nullable;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Random;

public class IPFSBlobStoreTest {

    // https://cid.ipfs.tech (https://github.com/multiformats/cid-utils-website)
    String HELLO_CIDv0 = "QmXV7pL1CB7A8Tzk7jP2XE9kRyk8HZd145KDptdxzmNLfu";
    String HELLO_CIDv1_RAW = "bafkreiefh74toyvanxn7oiwe5pu53vtnr5r53lvjp5jbypwmednhzf3aea";
    String HELLO_CID_IDENTITY = "bafyaafikcmeaeeqnnbswy3dpfqqho33snrsaugan";

    static @Nullable IPFSBlobStore ipfs;

    @BeforeClass
    public static void setUp() {
        if (Net.portAvailable(5001)) ipfs = new IPFSBlobStore(new IPFS("/ip4/127.0.0.1/tcp/5001"));
    }

    @Test
    public void loadHelloCIDv0() throws IOException {
        if (ipfs == null) return;
        var bytes = ignoreSocketTimeoutException(() -> ipfs.load(Cid.decode(HELLO_CIDv0)));
        if (bytes != null) assertThat(new String(bytes.read())).isEqualTo("hello, world\n");
    }

    @Test
    public void loadHelloCIDv1() throws IOException {
        if (ipfs == null) return;
        var bytes = ignoreSocketTimeoutException(() -> ipfs.load(Cid.decode(HELLO_CIDv1_RAW)));
        if (bytes != null) assertThat(new String(bytes.read())).isEqualTo("hello, world\n");
    }

    @Test
    public void loadHelloCIDv1Identity() throws IOException {
        if (ipfs == null) return;
        var bytes = ipfs.load(Cid.decode(HELLO_CID_IDENTITY));
        assertThat(new String(bytes.read())).isEqualTo("hello, world\n");
    }

    @Test
    public void storeHello() throws IOException {
        if (ipfs == null) return;
        var cid = ipfs.store(ByteSource.wrap("hello, world\n".getBytes()));
        assertThat(cid.toString()).isEqualTo(HELLO_CIDv1_RAW);
        assertThat(cid.codec).isEqualTo(Cid.Codec.Raw);
        assertThat(cid.version).isEqualTo(1);
    }

    @Test
    public void storeLoadRandom() throws IOException {
        if (ipfs == null) return;
        var bytes = generateRandomBytes(1024);
        var cid = ipfs.store(ByteSource.wrap(bytes));
        assertThat(cid.version).isEqualTo(1);
        var loaded = ipfs.load(cid);
        assertThat(loaded.read()).isEqualTo(bytes);
    }

    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        Random random = new Random();
        random.nextBytes(bytes);
        return bytes;
    }

    private @Nullable ByteSource ignoreSocketTimeoutException(
            CheckedSupplier<ByteSource, IOException> r) throws IOException {
        try {
            return r.get();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                return null;
            } else throw e;
        }
    }
}
