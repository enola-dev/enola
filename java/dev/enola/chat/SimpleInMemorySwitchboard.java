/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.chat;

import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.common.context.TLC;
import dev.enola.data.id.MultibaseIRI;
import dev.enola.identity.SubjectContextKey;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

@ThreadSafe
public class SimpleInMemorySwitchboard implements Switchboard {

    private final Queue<Consumer<Message>> consumers = new ConcurrentLinkedQueue<>();
    final Queue<Message> messages = new ConcurrentLinkedQueue<>();

    @Override
    public synchronized void post(Message.Builder builder) {
        if (builder.id() == null) builder.id(MultibaseIRI.random());
        if (builder.from() == null) builder.from(TLC.get(SubjectContextKey.USER));
        if (builder.createdAt() == null) builder.createdAt(Instant.now());
        if (builder.modifiedAt() == null) builder.modifiedAt(builder.createdAt());
        var message = builder.build();
        messages.add(message);

        // TODO: Accept Messages in a separate new thread per Consumer
        consumers.forEach(c -> c.accept(message));
    }

    @Override
    public synchronized void watch(Consumer<Message> consumer) {
        messages.forEach(consumer);
        consumers.add(consumer);
    }
}
