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

import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;

import java.util.Objects;

/**
 * An alternative (wrapper, actually) over {@link io.ipfs.multihash.Multihash} which "remembers" its
 * encoding base.
 */
public final class MultihashWithMultibase {
    // TODO extends Multihash ?

    private final Multibase.Base multibase;
    private final Multihash multihash;

    public static MultihashWithMultibase decode(String encoded) {
        Multibase.Base base;
        if (encoded.length() == 46 && encoded.startsWith("Qm"))
            // TODO Base58BTC or Base58Flickr ?
            base = Multibase.Base.Base58BTC;
        else base = Multibase.Base.lookup(encoded);
        return new MultihashWithMultibase(base, Multihash.decode(encoded));
    }

    private MultihashWithMultibase(Multibase.Base multibase, Multihash decode) {
        this.multibase = multibase;
        this.multihash = decode;
    }

    // TODO Give this method a better name...
    public MultihashWithMultibase copy(byte[] bytes) {
        var newMultihash = new Multihash(multihash.getType(), bytes);
        return new MultihashWithMultibase(multibase, newMultihash);
    }

    public Multihash multihash() {
        return multihash;
    }

    public Multibase.Base multibase() {
        return multibase;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MultihashWithMultibase other)) return false;
        return multihash.equals(other.multihash) && multibase.equals(other.multibase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(multihash, multibase);
    }

    @Override
    public String toString() {
        return Multihashes.toString(multihash, multibase);
    }
}
