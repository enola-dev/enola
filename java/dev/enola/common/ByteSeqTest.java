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

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.util.UUID;

public class ByteSeqTest {

    @Test
    public void byteArray() {
        var array = new byte[] {1, 2, 3};
        var id = ByteSeq.from(array);

        assertThat(id.size()).isEqualTo(3);
        assertThat(id.toBytes()).isEqualTo(array);
        assertThat(id.get(1)).isEqualTo(2);
        assertThat(id.hashCode()).isEqualTo(30817);
        assertThat(id.equals(ByteSeq.from(array))).isTrue();

        array[1] = 7;
        assertThat(id.get(1)).isEqualTo(2);
    }

    @Test
    public void uuid() {
        var uuid = UUID.randomUUID();
        var id = ByteSeq.from(uuid);

        assertThat(id.size()).isEqualTo(16);
        assertThat(id.toUUID()).isEqualTo(uuid);
        assertThat(id.toUUID().toString().length()).isEqualTo(36);
    }

    @Test
    public void random() {
        var r = ByteSeq.random(17);
        assertThat(r.size()).isEqualTo(17);
    }

    /*
        @Test
        public void protobufByteString() {
            var byteString = ByteString.copyFromUtf8("hello, world 😃");
            var id = ByteSeq.from(byteString);

            assertThat(id.size()).isEqualTo(17);
            assertThat(id.hashCode()).isEqualTo(734434573);
            assertThat(id.toByteString()).isEqualTo(byteString);
        }
    */

    @Test
    public void compare() {
        var id1 = ByteSeq.from(new byte[] {1});
        var id2 = ByteSeq.from(new byte[] {1, 2});

        assertThat(id1.compareTo(id2)).isEqualTo(-1);
    }

    @Test
    public void empty() {
        assertThat(ByteSeq.EMPTY.size()).isEqualTo(0);
        assertThat(ByteSeq.EMPTY.toBytes()).isEqualTo(new byte[0]);
        assertThat(ByteSeq.EMPTY.hashCode()).isEqualTo(1);
        // TODOassertThat(ByteSeq.EMPTY.toUUID().toString()).isEqualTo("...");
    }
}
