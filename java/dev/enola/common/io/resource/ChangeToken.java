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

/**
 * Change Token ("Tag") for {@link ReadableResource#changeToken()}.
 *
 * <p>This is named a "change token" and not "version" (or "revision") because that might imply
 * something “numeric” - but this is explicitly NOT intended to be used for “is it newer or older”
 * comparison, only “has it changed”. It's also not called a "fingerprint" because that might imply
 * it's derived only from the content itself (like a hash), whereas this may be based on more than
 * that, e.g. additional metadata.
 */
// TODO Should implementations also hold and compare the ReadableResource? Or even just its IRI?
public interface ChangeToken { // skipcq: JAVA-E1041

    /**
     * Check is this ChangeToken is different from that other ChangeToken.
     *
     * <p>If it's not sure, prefer "erring on the side of caution" by returning true instead of
     * false. That's why two {@link #NOT_AVAILABLE} are considered representing (possibly) different
     * resource contents (returns true).
     */
    boolean isDifferent(ChangeToken other);

    /**
     * String representation of this ChangeToken, (only) for {@link
     * ReadableResource#isDifferent(String)}.
     *
     * <p>Do not interpret the content of this String. It's intended to be used completely "opaque",
     * and only for before &amp; after comparison, on a Resource from the same URI. Implementations
     * are encouraged to return strings which do not (directly) "look like something familiar", to
     * avoid users relying on implementation details.
     */
    String toString();

    ByteSeq toBytes();

    /**
     * Constant (singleton) for "not available" change tokens.
     *
     * <p>Returned by {@link ReadableResource#changeToken()} when it's impossible to obtain a {@link
     * ChangeToken} e.g. due to internal technical errors, including because the URI points to a
     * non-existing resource. Its {@link #isDifferent(ChangeToken)} always returns true.
     */
    ChangeToken NOT_AVAILABLE =
            new ChangeToken() {
                @Override
                public boolean isDifferent(ChangeToken other) {
                    return true;
                }

                @Override
                public String toString() {
                    return "N/A";
                }

                @Override
                public ByteSeq toBytes() {
                    return ByteSeq.EMPTY;
                }

                @Override
                public boolean equals(Object obj) {
                    return obj == NOT_AVAILABLE;
                }

                @Override
                public int hashCode() {
                    return System.identityHashCode(this);
                }
            };
}
