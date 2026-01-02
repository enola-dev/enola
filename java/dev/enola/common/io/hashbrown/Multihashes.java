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

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;

import java.io.IOException;

/** Extension methods for {@link io.ipfs.multihash.Multihash}. */
public final class Multihashes {

    public static Multihash hash(ByteSource byteSource, Multihash.Type type) throws IOException {
        var hashFunction = Multihashes.toGuavaHashFunction(type);
        var hashCode = byteSource.hash(hashFunction);
        var actualBytes = hashCode.asBytes();
        return new Multihash(type, actualBytes);
    }

    public static HashFunction toGuavaHashFunction(Multihash.Type type) {
        return switch (type) {
            // NB: Please BEWARE of what types are added here; the (sub)selection is intentional!

            // TODO Support blake3, see https://github.com/enola-dev/enola/issues/1125

            // TODO Support murmur3-x64-64, https://github.com/multiformats/java-multihash/pull/43
            //   BUT beware of https://github.com/google/guava/issues/3493, that's worrying!
            //   case murmur3_x64_128 ->  Hashing.murmur3_128();
            // PS: Not suitable, as it's too tiny: case murmur3 -> Hashing.murmur3_32_fixed();

            // TODO Support AES-CMAC https://developers.google.com/tink/supported-key-types#mac ?
            //   Pending https://github.com/multiformats/multicodec/issues/368
            //   Using https://developers.google.com/tink/protect-data-from-tampering#java

            case sha2_256 -> Hashing.sha256();
            case sha2_512 -> Hashing.sha512();

            // TODO Add additional external libraries for other supported types?
            //   See https://github.com/multiformats/java-multihash/blob/master/README.md#usage
            //   from https://github.com/multiformats/java-multihash/pull/42/files
            //   for https://github.com/multiformats/java-multihash/issues/41.

            // See https://github.com/google/guava/issues/5990#issuecomment-2571350434 ...
            // SHA-224 (sha2-224) available in JDK, not available in Multibase table; so N/A
            // SHA-384 (sha2-384) available in JDK (but not Guava), and Multibase table, not API
            // SHA-512/224 (sha2-512-224) available in JDK (but not Guava), and Multibase table !API
            // SHA-512/256 (sha2-512-256) available in JDK (but not Guava), and Multibase table !API
            // NB https://github.com/google/guava/issues/5990
            // NB https://github.com/google/guava/issues/938
            //
            // NB https://github.com/google/guava/issues/3960
            // SHA3-224
            // SHA3-256
            // SHA3-384
            // SHA3-512
            // Note "sha3 not being a very good choice" on
            // https://github.com/google/guava/issues/3960#issuecomment-1080775346;
            // and https://developers.google.com/tink/protect-data-from-tampering#java
            // "recommend the HMAC_SHA256" (=sha2-256).

            // @Deprecated case md5 -> Hashing.md5();
            // @Deprecated case sha1 -> Hashing.sha1();

            default -> throw new IllegalArgumentException("Unsupported Multihash type: " + type);
        };
    }

    public static String toString(Multihash multihash, Multibase.Base base) {
        return Multibase.encode(base, multihash.toBytes());
    }

    public static Multihash fromString(String string) {
        return Multihash.decode(string);
    }

    public static String example(Multihash.Type type, Multibase.Base base) {
        var bytes = new byte[type.length];
        // for (int i = 0; i < type.length; i++) bytes[i] = (byte) 7;
        var multihash = new Multihash(type, bytes);
        return toString(multihash, base);
    }

    private Multihashes() {}
}
