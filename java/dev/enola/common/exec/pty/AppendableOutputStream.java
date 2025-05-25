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
package dev.enola.common.exec.pty;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * An OutputStream that writes to an Appendable. This class bridges the gap between byte-oriented
 * output streams and character-oriented appendable interfaces, allowing byte data to be written to
 * a character-based destination like a StringBuilder, Writer, or CharBuffer.
 */
class AppendableOutputStream extends OutputStream {
    // TODO If kept, then later move this to dev.enola.common.io

    // TODO Iff actually used, FIXME bugs in this class, see test, and TODO in close()

    private final Appendable appendable;
    private final CharsetDecoder decoder;
    private final CharBuffer charBuffer; // A buffer to hold decoded characters

    // TODO Rethink sizing - just max. number of bytes of largest multi-byte character encoding?
    private static final int DEFAULT_CHAR_BUFFER_SIZE = 1024;

    /**
     * Constructs an AppendableOutputStream with the specified Appendable and charset.
     *
     * @param appendable The Appendable to which bytes will be written as characters.
     * @param charset The Charset to use for decoding bytes into characters.
     */
    public AppendableOutputStream(Appendable appendable, Charset charset) {
        this.appendable = appendable;
        this.charBuffer = CharBuffer.allocate(DEFAULT_CHAR_BUFFER_SIZE);

        this.decoder = charset.newDecoder();
        // TODO This is wrong and does not fix the @Test euroSignWithThreeByteWrites
        this.decoder.onMalformedInput(CodingErrorAction.REPLACE);
        this.decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
    }

    /**
     * Writes the specified byte to this output stream.
     *
     * @param b The byte to write.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(int b) throws IOException {
        byte[] singleByte = {(byte) b};
        write(singleByte, 0, 1);
    }

    /**
     * Writes {@code len} bytes from the specified byte array starting at offset {@code off} to this
     * output stream. The bytes are decoded into characters using the specified charset and then
     * appended to the underlying Appendable.
     *
     * @param b The data.
     * @param off The start offset in the data.
     * @param len The number of bytes to write.
     * @throws IOException If an I/O error occurs.
     * @throws IndexOutOfBoundsException If {@code off} is negative, {@code len} is negative, or
     *     {@code off+len} is greater than the length of the array {@code b}.
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException("Offset or length out of bounds.");
        }
        if (len == 0) {
            return; // Nothing to write
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(b, off, len);

        // Decode bytes into characters
        // A single decode call might not consume all bytes or produce all characters
        // so we loop until the byte buffer is empty.
        while (byteBuffer.hasRemaining()) {
            var result = decoder.decode(byteBuffer, charBuffer, false); // false for endOfInput
            if (result.isUnderflow()) {
                // Not enough bytes to complete a character.
                // Decoder will hold partial bytes and resume on next call.
                break;
            } else if (result.isOverflow()) {
                // CharBuffer is full, append its content and clear for more chars.
                charBuffer.flip(); // Prepare for reading
                appendable.append(charBuffer);
                charBuffer.clear(); // Prepare for writing
            } else if (result.isError()) {
                // Malformed input or unmappable character
                // Throws MalformedInputException or UnmappableCharacterException
                result.throwException();
            }
        }

        // Append any characters that were decoded into the charBuffer during this write call
        charBuffer.flip();
        if (charBuffer.hasRemaining()) {
            appendable.append(charBuffer);
        }
        charBuffer.compact(); // Compact remaining (undecoded) characters to the beginning
    }

    @Override
    public void flush() throws IOException {
        // Tell the decoder that there is no more input (`endOfInput = true`).
        // This forces it to decode any remaining buffered bytes, producing replacement
        // characters for any truly incomplete sequences.
        CoderResult result = decoder.decode(ByteBuffer.allocate(0), charBuffer, true);
        if (result.isError()) {
            result.throwException();
        }
        charBuffer.flip();
        if (charBuffer.hasRemaining()) {
            appendable.append(charBuffer);
        }
        charBuffer.clear();

        if (appendable instanceof java.io.Flushable flushable) flushable.flush();
    }

    @Override
    public void close() throws IOException {
        // TODO FIXME NOPE flush(); // Ensure all buffered characters are written
        // Reset the decoder's internal state for potential reuse (though stream is closed)
        decoder.reset();

        if (appendable instanceof java.io.Closeable closeable) closeable.close();
    }
}
