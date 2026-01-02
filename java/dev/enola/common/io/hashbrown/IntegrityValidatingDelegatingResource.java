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
package dev.enola.common.io.hashbrown;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IntegrityValidatingDelegatingResource extends DelegatingResource {

    // TODO This needs to improved to re-hash() when Resource.version() [content] changes!
    // (For both scenarios; when it was valid, and when it was not.)

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
            var multihash = MultihashWithMultibase.decode(integrity);
            return new IntegrityValidatingDelegatingResource(original, multihash);
        }
    }

    private final MultihashWithMultibase expectedHash;
    private final AtomicBoolean validated = new AtomicBoolean(false);
    private final Lock validationLock = new ReentrantLock();

    public IntegrityValidatingDelegatingResource(
            Resource delegate, MultihashWithMultibase expectedHash) {
        super(delegate);
        this.expectedHash = expectedHash;
    }

    @Override
    public ByteSource byteSource() {
        ensureValidated();
        return delegate.byteSource();
    }

    @Override
    public CharSource charSource() {
        ensureValidated();
        return delegate.charSource();
    }

    private void ensureValidated() {
        if (validated.get()) {
            return;
        }

        validationLock.lock();
        try {
            if (!validated.get()) {
                validate();
                validated.set(true);
            }
        } finally {
            validationLock.unlock();
        }
    }

    private void validate() {
        try {
            var resourceHasher = new ResourceHasher();
            var actualHash = resourceHasher.hash(delegate, expectedHash.multihash().getType());
            if (!expectedHash.multihash().equals(actualHash)) {
                throw new IntegrityViolationException(
                        "Expected "
                                + expectedHash
                                + " but got "
                                + Multihashes.toString(actualHash, expectedHash.multibase()));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
