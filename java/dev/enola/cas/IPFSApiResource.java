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

import dev.enola.common.io.resource.*;

import io.ipfs.cid.Cid;

import java.io.IOException;
import java.net.URI;

/** <a href="https://ipfs.tech/">IPFS</a> Resource, via an IPFS Kubo RPC API. */
public class IPFSApiResource extends BaseResource implements ReadableResource {

    // TODO implement WritableResource

    public static class Provider implements ResourceProvider {
        private final IPFSBlobStore ipfs;

        public Provider(IPFSBlobStore ipfs) {
            this.ipfs = ipfs;
        }

        @Override
        public Resource getResource(URI uri) {
            if (isIPFS(uri))
                return new ReadableButNotWritableDelegatingResource(new IPFSApiResource(ipfs, uri));
            else return null;
        }
    }

    private static boolean isIPFS(URI uri) {
        return "ipfs".equals(uri.getScheme());
    }

    private static void check(URI uri) {
        if (!isIPFS(uri)) throw new IllegalArgumentException(uri.toString());
    }

    private final IPFSBlobStore ipfs;

    public IPFSApiResource(IPFSBlobStore ipfs, URI uri) {
        super(uri);
        check(uri);
        this.ipfs = ipfs;
    }

    @Override
    public ByteSource byteSource() {
        try {
            return ipfs.load(Cid.decode(uri().getAuthority()), uri().getPath());
        } catch (IOException e) {
            return new ErrorByteSource(e);
        }
    }
}
