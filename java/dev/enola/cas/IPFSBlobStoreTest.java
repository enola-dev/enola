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

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.OkHttpResource;

import io.ipfs.cid.Cid;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class IPFSBlobStoreTest {

    public @Rule SingletonRule singleton = SingletonRule.$(MediaTypeProviders.set());

    @Test
    public void hello() throws IOException {
        var ipfs =
                new IPFSBlobStore(
                        new IPFSResource.Provider(
                                new OkHttpResource.Provider(), "http://localhost:8080/ipfs/"));
        var bytes = ipfs.load(Cid.decode("QmXV7pL1CB7A8Tzk7jP2XE9kRyk8HZd145KDptdxzmNLfu"));
        assertThat(new String(bytes.read())).isEqualTo("hello, world\n");
    }
}
