/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
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

import dev.enola.common.io.hashbrown.Multihashes;
import dev.enola.common.io.hashbrown.ResourceHasher;

import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

public interface ReadableResource extends AbstractResource {

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
     * Last Modified date time (if known). Implemented e.g. via a File's last modified (not created
     * or accessed) time, or a remote resource's <tt>Last-Modified</tt> HTTP Header. Typically used
     * for cache invalidation to determine if the resource version is the same as a previously read
     * one. Some implementations may well not provide this!
     */
    @Deprecated // TODO Remove lastModifiedIfKnown() once revision() is implemented
    default Optional<Instant> lastModifiedIfKnown() {
        return Optional.empty();
    }

    /**
     * Fingerprint of content. Implementations may e.g. be based on:
     *
     * <ul>
     *   <li><a href="https://en.wikipedia.org/wiki/Stat_(system_call)"><tt>mtime</tt> Timestamp</a>
     *       of a File or Directory
     *   <li><a href="https://en.wikipedia.org/wiki/List_of_HTTP_header_fields">HTTP
     *       <tt>Last-Modified</tt></a>
     *   <li><a href="https://en.wikipedia.org/wiki/HTTP_ETag">HTTP Etag</a>
     *   <li><a href="https://en.wikipedia.org/wiki/Merkle_tree">Merkle tree</a>, e.g. of a
     *       Directory
     *   <li>Hash (checksum) of content
     *   <li>Git SHA revision
     *   <li>IPFS URL CID
     *   <li>...
     * </ul>
     *
     * <p>Intended to be used as "change token" (key), for change detection. Do not interpret the
     * content of this String. It's intended to be used completely "opaque", and only for before &
     * after comparison, on a Resource from the same URI. Implementations are encouraged to return
     * strings which do not (directly) "look like" something familiar, as above - to avoid users
     * relying on implementation details.
     *
     * <p>It's only based on {@link #byteSource()} (irrelevant of encoding and thus {@link
     * #charSource()}), so it ignores {@link #uri()} and {@link #mediaType()}. Ergo, 2 resources
     * with the same bytes content have the same fingerprint, even if they have different URIs. But
     * that's if and only if the objects are of the same (Java implementation) type - separate
     * implementations may create different fingerprints.
     *
     * <p>Named "fingerprint" and not "version", because that might imply “numeric” - but this is
     * explicitly NOT intended to be used for “is it newer or older” comparison, only “has it
     * changed”.
     *
     * <p>Fingerprint calculation could be an expensive operation. For example, obtaining a checksum
     * of a large file (if that's how fingerprinting was implemented) would require reading that
     * entire file.
     *
     * <p>Fingerprints of a resource may or may not be cached, or be cached for a certain time. This
     * is entirely dependent on the implementation.
     *
     * <p>The default implementations currently returns a Multibase RFC-4648 Base64url encoded
     * SHA2-512 CHF MAC. This default may be changed at any time, without notice. At least some
     * implementations of this interface may well use entirely solutions.
     *
     * @return Fingerprint, or the special value "N/A" when it's impossible to calculate a
     *     fingerprint e.g. due to internal technical errors, or because the URI points to a
     *     non-existing resource. Implementations should never return null or an empty String.
     */
    // TODO rename fingerprint() to changeToken()
    // TODO introduce an actual interface or class ChangeToken
    default String fingerprint() {
        try {
            // TODO Reconsider (default) hash type - how much faster is MD5 than SHA-512?!
            var multihash = new ResourceHasher().hash(this, Multihash.Type.sha2_512);
            return Multihashes.toString(multihash, Multibase.Base.Base64Url);
        } catch (IOException e) {
            return "N/A";
        }
    }
}
