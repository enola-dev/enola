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
package dev.enola.common.protobuf;

import static com.google.common.net.MediaType.create;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.io.mediatype.MediaTypes.normalize;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_BINARY;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;

import org.junit.Rule;
import org.junit.Test;

public class ProtobufMediaTypesTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new ProtobufMediaTypes()));

    @Test
    public void testProtobufMediaTypesAlternatives() {
        assertThat(normalize(create("application", "vnd.google.protobuf")))
                .isEqualTo(PROTOBUF_BINARY);
        assertThat(normalize(create("application", "x-protobuf"))).isEqualTo(PROTOBUF_BINARY);
    }
}
