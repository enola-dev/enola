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
package dev.enola.tools.proto;

import static com.google.common.io.Resources.getResource;
import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import com.google.protobuf.Timestamp;

import org.junit.Test;

// TODO(vorburger) Upstream this!
public class TextProtoValidatorTest {
    @Test
    public void testValidation() {
        // OK
        // TODO(vorburger) Should not require Timestamp.newBuilder(), but proto-file + proto-message
        // TODO(vorburger) Should not return null, but Timestamp instance, which needs to be
        // asserted
        // for equalsTo.
        assertThat(
                        new TextProtoValidator()
                                .validate(
                                        getResource("dev/enola/tools/proto/ok.textproto"),
                                        Timestamp.newBuilder()))
                .isNull();

        // NOK
        assertThat(
                        assertThrows(
                                IllegalArgumentException.class,
                                () ->
                                        new TextProtoValidator()
                                                .validate(
                                                        getResource(
                                                                "dev/enola/tools/proto/nok.textproto"),
                                                        Timestamp.newBuilder())))
                .hasCauseThat()
                .hasMessageThat()
                .contains("google.protobuf.Timestamp.bad");
    }
}
