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
            // @Deprecated case md5 -> Hashing.md5();
            // @Deprecated case sha1 -> Hashing.sha1();
            case sha2_256 -> Hashing.sha256();
            case sha2_512 -> Hashing.sha512();
            // Not suitable, as it's tiny: case murmur3 -> Hashing.murmur3_32_fixed();

            // TODO What about all other supported types?!
            //   See https://github.com/multiformats/java-multihash/issues/41 ...

            default -> throw new IllegalArgumentException("Unsupported Multihash type: " + type);
        };
    }

    public static String toString(Multihash multihash, Multibase.Base base) {
        return Multibase.encode(base, multihash.toBytes());
    }

    public static String example(Multihash.Type type, Multibase.Base base) {
        var bytes = new byte[type.length];
        // for (int i = 0; i < type.length; i++) bytes[i] = (byte) 7;
        var multihash = new Multihash(type, bytes);
        return toString(multihash, base);
    }

    private Multihashes() {}
}
