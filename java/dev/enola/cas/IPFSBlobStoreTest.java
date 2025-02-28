/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;

import io.ipfs.api.IPFS;
import io.ipfs.cid.Cid;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

public class IPFSBlobStoreTest {

    public @Rule SingletonRule singleton = SingletonRule.$(MediaTypeProviders.set());

    IPFSBlobStore ipfs = new IPFSBlobStore(new IPFS("/ip4/127.0.0.1/tcp/5001"));

    @Test
    public void hello() throws IOException {
        var bytes = ipfs.load(Cid.decode("QmXV7pL1CB7A8Tzk7jP2XE9kRyk8HZd145KDptdxzmNLfu"));
        assertThat(new String(bytes.read())).isEqualTo("hello, world\n");
    }

    @Test
    public void random() throws IOException {
        var bytes = generateRandomBytes(1024);
        var cid = ipfs.store(ByteSource.wrap(bytes));
        var loaded = ipfs.load(cid);
        assertThat(loaded.read()).isEqualTo(bytes);
    }

    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        Random random = new Random();
        random.nextBytes(bytes);
        return bytes;
    }
}
