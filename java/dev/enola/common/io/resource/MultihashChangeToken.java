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
package dev.enola.common.io.resource;

import dev.enola.common.ByteSeq;
import dev.enola.common.io.hashbrown.Multihashes;

import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;

/**
 * {@link Multihash}-based {@link ChangeToken}. Not intended to be used directly by Resource API
 * consumers, but only internally in some (but not all) implementations of {@link
 * ReadableResource#changeToken()} and {@link ReadableResource#isDifferent(String)}.
 */
/* Package Local, do not make public (or move into a io.resource.spi sub-package) */
record MultihashChangeToken(Multihash multihash) implements ChangeToken {

    MultihashChangeToken(String string) {
        this(Multihashes.fromString(string));
    }

    @Override
    public boolean isDifferent(ChangeToken other) {
        if (other instanceof MultihashChangeToken(Multihash otherMultihash)) {
            return !multihash.equals(otherMultihash);
        } else return true;
    }

    @Override
    public String toString() {
        return Multihashes.toString(multihash, Multibase.Base.Base64Url);
    }

    @Override
    public ByteSeq toBytes() {
        return ByteSeq.from(multihash.toBytes());
    }
}
