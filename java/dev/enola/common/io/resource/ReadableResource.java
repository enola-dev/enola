/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

import static dev.enola.common.io.resource.SPI.missingCharsetExceptionSupplier;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import dev.enola.common.ByteSeq;
import dev.enola.common.io.hashbrown.ResourceHasher;

import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public interface ReadableResource extends AbstractResource {

    Logger LOGGER = LoggerFactory.getLogger(ReadableResource.class);

    ByteSource byteSource();

    // TODO Consider replacing or integrating this with Converter?!
    default CharSource charSource() {
        // Workaround for java.lang.NoSuchMethodError: 'java.util.Optional
        // com.google.common.base.Optional.toJavaUtil()' due to classpath hell :(
        var charset = java.util.Optional.ofNullable(mediaType().charset().orNull());
        return byteSource()
                .asCharSource(charset.orElseThrow(missingCharsetExceptionSupplier(this)));
    }

    // NO contentLength() because ByteSource already has a size() + sizeIfKnown()

    /**
     * {@link ChangeToken} of this resource's content. Implementations may e.g. be based on:
     *
     * <ul>
     *   <li><a href="https://en.wikipedia.org/wiki/Stat_(system_call)"><code>mtime</code>
     *       Timestamp</a> of a File or Directory
     *   <li><a href="https://en.wikipedia.org/wiki/List_of_HTTP_header_fields">HTTP <code>
     *       Last-Modified</code></a>
     *   <li><a href="https://en.wikipedia.org/wiki/HTTP_ETag">HTTP Etag</a>
     *   <li><a href="https://en.wikipedia.org/wiki/Merkle_tree">Merkle tree</a>, e.g. of a
     *       Directory
     *   <li>Hash (checksum) of content
     *   <li>Git SHA revision
     *   <li>IPFS URL CID
     *   <li>...
     * </ul>
     *
     * <p>Intended to be used for change detection, like this:
     *
     * <ol>
     *   <li>Obtain the ChangeToken of a Resource using this method.
     *   <li>Store it somewhere; either in-memory as a Java object, or possibly externally in its
     *       {@link ChangeToken#toString()} or {@link ChangeToken#toBytes()} form.
     *   <li>Later, to check if the Resource at this same (!) {@link #uri()} has changed, get
     *       another ChangeToken using this method
     *   <li>To compare the original and current one, use {@link
     *       ChangeToken#isDifferent(ChangeToken)}, or {@link #isDifferent(String)} or {@link
     *       #isDifferent(ByteSeq)}.
     * </ol>
     *
     * <p>Implementations may be based on {@link #byteSource()} (typically irrelevant of encoding
     * and thus {@link #charSource()}), and possibly metadata not available via the Resource API. It
     * may it ignore {@link #uri()} and {@link #mediaType()}. Ergo, 2 resources with the same bytes
     * content may have the same ChangeToken, even if they have different URIs, but this should not
     * be relied upon. ChangeTokens should only be interchange if and only if they are instances of
     * the same (Java implementation of) Resource type - separate implementations will create
     * different change tokens, which will always be considered different.
     *
     * <p>ChangeToken calculation could be an expensive operation. For example, obtaining a checksum
     * of a large file (if that's how a resource implements this method) would require reading that
     * entire file.
     *
     * <p>Resources may cache or may not cache ChangeTokens, or be cached for a certain time. This
     * is entirely dependent on the implementation.
     *
     * <p>The default implementations currently returns a Multibase RFC-4648 Base64url encoded
     * SHA2-512 CHF MAC. This default may be changed at any time, without notice. At least some
     * implementations of this interface may well use entirely different solutions.
     *
     * @return Change Token, never null.
     */
    default ChangeToken changeToken() {
        try {
            // TODO Reconsider (default) hash - is SHA2-256, or even MD5, much faster than SHA2-512?
            var multihash = new ResourceHasher().hash(this, Multihash.Type.sha2_512);
            return new MultihashChangeToken(multihash);
        } catch (IOException e) {
            LOGGER.warn("IOException while calculating ChangeToken for resource: {}", uri(), e);
            return ChangeToken.NOT_AVAILABLE;
        }
    }

    /**
     * Like {@link ChangeToken#isDifferent(ChangeToken)}, but given a String instead of an object.
     *
     * @param previousToString The output of calling {@link ChangeToken#toString()} on a ChangeToken
     *     previously obtained from {@link #changeToken()} for this Resource.
     * @return see {@link ChangeToken#isDifferent(ChangeToken)}
     */
    default boolean isDifferent(String previousToString) {
        if (Multibase.hasValidPrefix(previousToString))
            return changeToken().isDifferent(new MultihashChangeToken(previousToString));
        else return true;
    }

    default boolean isDifferent(ByteSeq previousBytes) {
        var previousMultihash = Multihash.deserialize(previousBytes.toBytes());
        return changeToken().isDifferent(new MultihashChangeToken(previousMultihash));
    }
}
