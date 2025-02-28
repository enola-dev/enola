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

import com.google.common.io.ByteSource;

import io.ipfs.api.IPFS;
import io.ipfs.api.NamedStreamable;
import io.ipfs.cid.Cid;

import java.io.IOException;

public class IPFSBlobStore implements BlobStore { // TODO , IdStore

    private final IPFS ipfs;

    public IPFSBlobStore(IPFS ipfs) {
        this.ipfs = ipfs;
    }

    @Override
    public Cid store(ByteSource source) throws IOException {
        var namedStreamable = new NamedStreamable.ByteArrayWrapper(source.read());
        var merkleNode = ipfs.add(namedStreamable);
        // TODO Why does it not work with version == 1 ?!
        return Cid.build(0, Cid.Codec.Raw, merkleNode.get(0).hash);
    }

    @Override
    public ByteSource load(Cid cid) throws IOException {
        return ByteSource.wrap(ipfs.cat(cid));
    }
}
