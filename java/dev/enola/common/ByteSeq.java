/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

import com.google.errorprone.annotations.Immutable;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

/**
 * Immutable Sequence of Bytes, of variable (but obviously fixed) length.
 *
 * <p>Typically intended to be used for "small"(-ish) size, like binary IDs, hashes, cryptographic
 * keys, and such things; do not use this for "very large BLOBs". The hashCode is cached.
 *
 * <p>The <code>com.google.protobuf.ByteString</code> is very similar - but we don't want to depend
 * on the ProtoBuf library JUST for having a type like this. Likewise, <code>
 * com.google.crypto.tink.util.Bytes</code> is similar.
 *
 * <p>This intentionally does <b>not</b> implement <code>Iterable&lt;Byte&gt;</code> to avoid boxing
 * overhead.
 */
@Immutable
public final class ByteSeq implements Comparable<ByteSeq> {

    // TODO Re-think if this is really the same as a com.google.common.io.ByteSource?!
    //   If concluding that it's not, then update JavaDoc to explain why...

    // TODO Should this extend com.google.common.io.ByteSource?!

    // TODO Should this have a static from(ByteSource) method?

    // TODO Add String toBase64() and static ID fromBase64(String data)
    //   using https://www.baeldung.com/java-base64-encode-and-decode

    // TODO Support substring (slice?) and concatenation, like Protobuf ByteString?

    // TODO Add a ByteBuffer asReadOnlyByteBuffer() method?

    // TODO Add startsWith(ByteSeq) and endsWith(ByteSeq) methods?

    // TODO Add asInputStream() method?

    public static final ByteSeq EMPTY = new ByteSeq(new byte[0]);

    public static final class Builder {
        private final byte[] bytes;

        // TODO private final int position = 0;

        private Builder(int size) {
            bytes = new byte[size];
        }

        public ByteSeq build() {
            // TODO if (position != bytes.length) throw new IllegalStateException();
            return new ByteSeq(bytes);
        }

        // TODO public Builder add(int integer) {

        public Builder add(ByteBuffer bb) {
            if (!bb.isReadOnly()) {
                throw new IllegalArgumentException("ByteBuffer !isReadOnly()");
            }
            // TODO Write to current position offset
            // TODO Take length as an argument
            // TODO Check for Overflow
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

    /**
     * New ByteSeq from a String, in UTF-8. Inverse of {@link #asString()}. More efficient than
     * (avoids 2nd re-copy) using {@link #from(byte[])} on {@link String#getBytes()}, and safer
     * because it avoids accidentally using the (non-fixed) platform default charset.
     */
    public static ByteSeq from(String string) {
        return from(string, StandardCharsets.UTF_8);
    }

    public static ByteSeq from(String string, Charset charset) {
        return new ByteSeq(string.getBytes(charset));
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

    // Stolen from java.util.UUID:
    private static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
    }

    public static ByteSeq random(int size) {
        // As in java.util.UUID#randomUUID:
        // (See also dev.enola.data.id.UUID_IRI constructor!)
        SecureRandom ng = Holder.numberGenerator;
        byte[] randomBytes = new byte[size];
        ng.nextBytes(randomBytes);
        return new ByteSeq(randomBytes);
    }

    @SuppressWarnings("Immutable") // Holy promise never to change the bytes!
    private final byte[] bytes;

    @SuppressWarnings("Immutable") // Holy promise never to use only as cache!
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
    // TODO Rename toUUID() to asUUID() for consistency with asString()?
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

    /** Return String from bytes decoded as UTF-8. Inverse of {@link #from(String)}. */
    public String asString() {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Returns {@code true} if the size is {@code 0}, {@code false} otherwise.
     *
     * @return true if this is zero bytes long
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Return debug information about this object.
     *
     * @see #asString()
     */
    @Override
    public String toString() {
        return "ByteSeq@" + Integer.toHexString(hashCode()) + "; size=" + size();
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
