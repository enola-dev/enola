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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class IPFSBlobStore implements BlobStore { // TODO , IdStore

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final IPFS ipfs;

    public IPFSBlobStore(IPFS ipfs) {
        this.ipfs = ipfs;
        try {
            LOG.info("IPFS version: {}", ipfs.version());
        } catch (IOException e) {
            LOG.error("Failed to retrieve IPFS version", e);
        }
    }

    @Override
    public Cid store(ByteSource source) throws IOException {
        try (var is = source.openStream()) {
            var namedStreamable = new NamedStreamable.InputStreamWrapper(is);
            var merkleNode = ipfs.add(namedStreamable);
            // TODO Why does it not work with version == 1 ?!
            //   https://github.com/ipfs-shipyard/java-ipfs-http-client/issues/235
            return Cid.build(0, Cid.Codec.Raw, merkleNode.get(0).hash);
        }
    }

    @Override
    public ByteSource load(Cid cid) throws IOException {
        return ByteSource.wrap(ipfs.cat(cid));
    }
}
