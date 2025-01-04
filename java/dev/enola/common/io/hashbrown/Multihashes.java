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

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;

/** Extension methods for {@link io.ipfs.multihash.Multihash}. */
public final class Multihashes {

    public static HashFunction toGuavaHashFunction(Multihash multihash) {
        var type = multihash.getType();
        return switch (type) {
            case md5 -> Hashing.md5();
            // TODO Add all other supported types...
            default -> throw new IllegalArgumentException("Unsupported Multihash type: " + type);
        };
    }

    public static String toString(Multihash multihash, Multibase.Base base) {
        return Multibase.encode(base, multihash.toBytes());
    }

    private Multihashes() {}
}
