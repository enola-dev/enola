/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.common.protobuf;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import com.google.protobuf.Timestamp;

import org.junit.Test;

import java.io.IOException;

public class ProtoIOTest extends AbstractProtoTest {
    public ProtoIOTest() {
        super("ok.textproto", Timestamp.newBuilder());
    }

    @Test
    public void testValidation() throws IOException {
        // OK
        Timestamp timestamp =
                new ProtoIO()
                        .merge(classpath("ok.textproto"), Timestamp.newBuilder(), Timestamp.class);
        assertThat(timestamp)
                .isEqualTo(Timestamp.newBuilder().setSeconds(123).setNanos(456).build());

        // NOK
        assertThat(
                        assertThrows(
                                ProtoIO.TextParseException.class,
                                () ->
                                        new ProtoIO()
                                                .merge(
                                                        classpath("nok.textproto"),
                                                        Timestamp.newBuilder())))
                .hasMessageThat()
                .contains("google.protobuf.Timestamp.bad");

        // new ProtoIO().merge(classpath("nok.textproto"), Timestamp.newBuilder());
    }
}
