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
package dev.enola.common;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

/**
 * Immutable Sequence of Bytes, of variable length.
 *
 * <p>Typically intended to be used for "small"(-ish) size, like binary IDs, hashes, cryptographic
 * keys, and such things; do not use this for "large BLOBs" (like images or so). The hashCode is
 * cached.
 */
public final class ByteSeq implements Comparable<ByteSeq> {

    public static final ByteSeq EMPTY = new ByteSeq(new byte[0]);

    public static final class Builder {
        private final byte[] bytes;

        private Builder(int size) {
            bytes = new byte[size];
        }

        public ByteSeq build() {
            return new ByteSeq(bytes);
        }

        public Builder add(ByteBuffer bb) {
            if (!bb.isReadOnly()) {
                throw new IllegalArgumentException("ByteBuffer !isReadOnly()");
            }
            bb.get(bytes);
            return this;
        }
    }

    public static Builder builder(int size) {
        return new Builder(size);
    }

    // TODO public static final ByteSeq fromMultibase(String multibase) {

    /**
     * Create a ByteSeq from an array of bytes. Prefer using the Builder instead of this, to avoid
     * the implementation have to copy the array.
     *
     * @param bytes Bytes (which will be copied)
     * @return the ByteSeq
     */
    public static ByteSeq from(byte[] bytes) {
        if (bytes.length == 0) {
            return EMPTY;
        }
        return new ByteSeq(Arrays.copyOf(bytes, bytes.length));
    }

    /*
    * Create a ByteSeq from a Protocol Buffer "bytes" field.
    *
    * @param proto the ByteString
    * @return the ByteSeq
    public static ByteSeq from(ByteString proto) {
       return ByteSeq.builder(proto.size()).add(proto.asReadOnlyByteBuffer()).build();
    }
    */

    // ? public static ByteSeq toByteSeq(Message proto) { return
    // ByteSeq.copyFrom(proto.toByteArray()); }

    public static ByteSeq from(UUID uuid) {
        var byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        byteBuffer.position(0);

        var builder = builder(16);
        builder.add(byteBuffer.asReadOnlyBuffer());
        return builder.build();
    }

    private final byte[] bytes;
    private transient int hashCode;

    private ByteSeq(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] toBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public int size() {
        return bytes.length;
    }

    public byte get(int index) {
        return bytes[index];
    }

    // public String toString(Base base) { return Multibase.encode(Base.Base32, id.toByteArray()); }
    /*
        public ByteString toByteString() {
            return ByteString.copyFrom(bytes);
        }
    */
    public UUID toUUID() {
        if (bytes.length != 16) {
            throw new IllegalStateException(
                    "toUUID() is currently only supported for length == 16, not: " + bytes.length);
        }
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Arrays.hashCode(bytes);
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        var other = (ByteSeq) obj;
        if (hashCode() != other.hashCode()) {
            return false;
        }
        return Arrays.equals(bytes, other.bytes);
    }

    @Override
    public int compareTo(ByteSeq other) {
        return Arrays.compare(bytes, other.bytes);
    }
}
