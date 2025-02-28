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

import static java.util.Objects.requireNonNull;

import com.google.common.io.ByteSource;

import io.ipfs.cid.Cid;

public class IPFS implements BlobStore { // TODO , IdStore

    private final IPFSResource.Provider ipfsResourceProvider;

    public IPFS(IPFSResource.Provider ipfsResourceProvider) {
        this.ipfsResourceProvider = ipfsResourceProvider;
    }

    @Override
    public Cid store(ByteSource source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteSource load(Cid cid) {
        return requireNonNull(ipfsResourceProvider.get("ipfs:" + cid)).byteSource();
    }
}
