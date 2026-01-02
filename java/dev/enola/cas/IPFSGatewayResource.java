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

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.resource.*;

import io.ipfs.cid.Cid;

import java.net.URI;

/**
 * <a href="https://ipfs.tech/">IPFS</a> Resource, via an IPFS HTTP Gateway.
 *
 * <p>This is read-only, as one cannot write to such a gateway.
 */
public class IPFSGatewayResource extends BaseResource implements ReadableResource {

    public static class Provider implements ResourceProvider {
        private final ResourceProvider httpResourceProvider;
        private final String ipfsGateway;

        public Provider(ResourceProvider httpResourceProvider, String ipfsGateway) {
            this.httpResourceProvider = httpResourceProvider;
            this.ipfsGateway = ipfsGateway;
        }

        @Override
        public Resource getResource(URI uri) {
            if (isIPFS(uri))
                return new ReadableButNotWritableDelegatingResource(
                        new IPFSGatewayResource(uri, httpResourceProvider, ipfsGateway));
            else return null;
        }
    }

    private static boolean isIPFS(URI uri) {
        return "ipfs".equals(uri.getScheme());
    }

    private static void check(URI uri, String gateway) {
        if (!isIPFS(uri)) throw new IllegalArgumentException(uri.toString());
        if (Strings.isNullOrEmpty(gateway))
            throw new IllegalStateException("IPFS HTTP Gateway is required");
        // Validate CID using parser from https://github.com/ipld/java-cid
        Cid.decode(uri.getAuthority());
    }

    private final ReadableResource httpResource;

    public IPFSGatewayResource(URI uri, ResourceProvider httpResourceProvider, String gateway) {
        super(uri);
        check(uri, gateway);
        this.httpResource = httpResourceProvider.getReadableResource(ipfs2http(uri, gateway));
    }

    public IPFSGatewayResource(
            URI uri, MediaType mediaType, ResourceProvider httpResourceProvider, String gateway) {
        super(uri, mediaType);
        check(uri, gateway);
        this.httpResource = httpResourceProvider.getReadableResource(ipfs2http(uri, gateway));
    }

    private String ipfs2http(URI ipfsURL, String gateway) {
        return gateway
                + (gateway.endsWith("/") ? "" : "/")
                + ipfsURL.getAuthority()
                + ipfsURL.getPath();
    }

    @Override
    public ByteSource byteSource() {
        return httpResource.byteSource();
    }
}
