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
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;

/**
 * Wany is a Wrapped {@link Any}. This is a useful type to pass around in-process to prevent
 * wasteful "too early" conversion to and back from Any.
 */
public class Wany {
    // TODO This isn't actually used anywhere, yet; use it! (Or delete it again.)

    private Any any;
    private Message message;

    private Wany() {}

    public static Wany of(Message message) {
        var wany = new Wany();
        wany.message = message;
        return wany;
    }

    public static Wany of(Any any) {
        var wany = new Wany();
        wany.any = any;
        return wany;
    }

    public MessageLite asMessage(Messages utility) {
        if (message == null) message = utility.toMessage(any);
        return message;
    }

    public Any asAny() {
        if (any == null) any = Any.pack(message);
        return any;
    }
}
