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
package dev.enola.common.io.hashbrown;

import com.google.common.hash.Hasher;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.*;

import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;

public class IntegrityValidatingDelegatingResource extends DelegatingResource {

    public static class Provider implements ResourceProvider {
        private final ResourceProvider delegatingResourceProvider;

        public Provider(ResourceProvider delegatingResourceProvider) {
            this.delegatingResourceProvider = delegatingResourceProvider;
        }

        @Override
        public Resource getResource(URI uri) {
            var original = delegatingResourceProvider.getResource(uri);
            if (original == null) return null;
            var integrity = URIs.getQueryMap(uri).get("integrity");
            if (integrity == null) return original;
            var multihash = Multihash.decode(integrity);
            return new IntegrityValidatingDelegatingResource(original, multihash);
        }
    }

    private final Multihash expectedHash;
    private boolean validated = false;

    public IntegrityValidatingDelegatingResource(Resource delegate, Multihash expectedHash) {
        super(delegate);
        this.expectedHash = expectedHash;
    }

    @Override
    public ByteSource byteSource() {
        validate();
        return delegate.byteSource();
    }

    @Override
    public CharSource charSource() {
        validate();
        return delegate.charSource();
    }

    private synchronized void validate() {
        if (validated) return;

        var delegateByteSource = delegate.byteSource();
        var hashFunction = Multihashes.toGuavaHashFunction(expectedHash);

        Hasher hasher;
        var optSize = delegateByteSource.sizeIfKnown();
        if (optSize.isPresent()) hasher = hashFunction.newHasher(Math.toIntExact(optSize.get()));
        else hasher = hashFunction.newHasher();

        try (var is = delegateByteSource.openBufferedStream()) {
            var read = is.read();
            while (read != -1) {
                hasher.putByte((byte) read);
                read = is.read();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        var hashCode = hasher.hash();
        var actualBytes = hashCode.asBytes();
        var actualMultihash = new Multihash(expectedHash.getType(), actualBytes);

        // TODO It would be useful if Multihash had an equalsTo() method to avoid byte array copy
        if (!expectedHash.equals(actualMultihash)) {
            // TODO Fix that this looses the "original" Base from ?integrity=..
            var expectedHashString = Multihashes.toString(expectedHash, Multibase.Base.Base64);
            var actualMultihashString =
                    Multihashes.toString(actualMultihash, Multibase.Base.Base64);
            throw new IntegrityViolationException(
                    "Expected " + expectedHashString + " but got " + actualMultihashString);
        }

        validated = true;
    }
}
