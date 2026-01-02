/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Anys {

    private static final Logger LOG = LoggerFactory.getLogger(Anys.class);

    public static <T extends com.google.protobuf.Message> T unpack(Any any, Class<T> clazz)
            throws InvalidProtocolBufferException {
        try {
            return any.unpack(clazz);
        } catch (InvalidProtocolBufferException e) {
            LOG.error("InvalidProtocolBufferException from Any.unpack(), use Any.is(): {}", any);
            throw e;
        }
    }

    private Anys() {}
}
