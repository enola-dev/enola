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

import com.google.common.io.ByteSource;

import io.ipfs.api.AddArgs;
import io.ipfs.api.IPFS;
import io.ipfs.api.NamedStreamable;
import io.ipfs.cid.Cid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// https://docs.ipfs.tech/reference/kubo/rpc/
/** <a href="https://ipfs.tech/">IPFS</a> Kubo RPC API client. */
public class IPFSBlobStore implements BlobStore {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    // TODO: Support IPLD <=> Thing API bridge; see https://github.com/enola-dev/enola/issues/777.

    private final IPFS ipfs;

    public IPFSBlobStore(IPFS ipfs) {
        this.ipfs = ipfs;
        try {
            LOG.info("{}", ipfs.version());
            LOG.info("IPFS {}", ipfs.version);
        } catch (IOException e) {
            LOG.error("Failed to retrieve IPFS version", e);
        }
    }

    @Override
    public ByteSource load(Cid cid) throws IOException {
        return ByteSource.wrap(ipfs.cat(cid));
    }

    public ByteSource load(Cid cid, String subPath) throws IOException {
        return ByteSource.wrap(ipfs.cat(cid, subPath));
    }

    @Override
    public Cid store(ByteSource source) throws IOException {
        try (var is = source.openStream()) {
            var namedStreamable = new NamedStreamable.InputStreamWrapper(is);
            // TODO .setHash("blake3") https://github.com/multiformats/java-multihash/issues/49
            var args = AddArgs.Builder.newInstance().setCidVersion(1).build();
            var merkleNode = ipfs.add(namedStreamable, args);
            return Cid.build(1, Cid.Codec.Raw, merkleNode.get(0).hash);
        }
    }

    // TODO Cid store(Tree of relative directories & files...)
}
