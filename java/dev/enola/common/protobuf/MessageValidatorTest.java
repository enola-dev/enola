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

import static com.google.common.truth.Truth.assertThat;

import com.google.protobuf.Timestamp;

import org.junit.Test;

public class MessageValidatorTest {

    MessageValidator<Void, Timestamp> testValidator =
            (ctx, ts, r) -> {
                // TODO Make getting Descriptor simpler & shorter...
                if (ts.getNanos() == 0)
                    r.add(
                            Timestamp.getDescriptor()
                                    .findFieldByNumber(Timestamp.NANOS_FIELD_NUMBER),
                            ">0!");
                if (ts.getSeconds() == 0)
                    r.add(
                            Timestamp.getDescriptor()
                                    .findFieldByNumber(Timestamp.SECONDS_FIELD_NUMBER),
                            ">0!");
            };

    @Test
    public void testValidate() {
        var v = new MessageValidators();
        v.register(testValidator, Timestamp.getDescriptor());
        assertThat(v.validate(Timestamp.getDefaultInstance()).toMessage().getValidationsCount())
                .isEqualTo(2);
    }
}
