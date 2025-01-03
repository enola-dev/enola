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
package dev.enola.common.io.resource;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import java.net.URI;

/**
 * <a href="https://ipfs.tech/">IPFS</a> Resource. TODO:
 *
 * <ol>
 *   <li>Does IPFS include MediaType? Otherwise hard-code it here...
 *   <li>Support IPLD <=> Thing API bridge...
 *   <li>Existing? https://github.com/ipld/java-cid and
 *       https://github.com/ipfs-shipyard/java-ipfs-http-client#dependencies
 *       com.github.ipfs:java-ipfs-api, com.github.multiformats:java-multiaddr; cid.jar,
 *       multibase.jar, multihash.jar ?
 *   <li>Support IPNS, but as ipns:// or as ipfs://ipns/ ? Also consider how that needs different
 *       caching than ipfs:
 *   <li>Support CAR files
 *   <li>Get Thing and add https://cid.ipfs.tech -like technical debugging info?
 *   <li>Support writing - via a WritableResource, or (probably) a separate API?
 *   <li>Detect IPFS in other links, using https://github.com/ipfs-shipyard/is-ipfs's algorithm
 * </ol>
 */
public class IPFSResource extends BaseResource implements ReadableResource {

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
                        new IPFSResource(uri, httpResourceProvider, ipfsGateway));
            else return null;
        }
    }

    private static boolean isIPFS(URI uri) {
        return "ipfs".equals(uri.getScheme());
    }

    private static void checkScheme(URI uri) {
        if (!isIPFS(uri)) throw new IllegalArgumentException(uri.toString());
    }

    private final ReadableResource httpResource;

    public IPFSResource(URI uri, ResourceProvider httpResourceProvider, String gateway) {
        super(uri);
        checkScheme(uri);
        this.httpResource = httpResourceProvider.getReadableResource(ipfs2http(uri, gateway));
    }

    public IPFSResource(
            URI uri, MediaType mediaType, ResourceProvider httpResourceProvider, String gateway) {
        super(uri, mediaType);
        checkScheme(uri);
        this.httpResource = httpResourceProvider.getReadableResource(ipfs2http(uri, gateway));
    }

    private String ipfs2http(URI ipfsURL, String gateway) {
        if (Strings.isNullOrEmpty(gateway))
            throw new IllegalStateException("IPFS Gateway is not configured");
        return gateway + ipfsURL.getAuthority() + ipfsURL.getPath();
    }

    @Override
    public ByteSource byteSource() {
        return httpResource.byteSource();
    }
}
