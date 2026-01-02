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

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import java.time.Instant;

/**
 * Utilities to help create/manipulate {@code protobuf/timestamp.proto} on Java 8+. NB: {@link
 * com.google.protobuf.util.Timestamps} does not support {@link Instant}, yet.
 */
public final class Timestamps2 {
    public static Timestamp fromInstant(Instant i) {
        var ts =
                Timestamp.newBuilder().setSeconds(i.getEpochSecond()).setNanos(i.getNano()).build();
        Timestamps.checkValid(ts);
        return ts;
    }

    public static Instant toInstant(Timestamp ts) {
        Timestamps.checkValid(ts);
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }

    public static long epochMillis(Timestamp ts) {
        // TODO This could be optimized to save the (useless) intermediate Instant object
        return toInstant(ts).toEpochMilli();
    }

    private Timestamps2() {}
}
