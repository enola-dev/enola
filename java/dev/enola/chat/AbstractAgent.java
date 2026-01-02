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

import dev.enola.identity.Subject;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;

import java.util.function.Consumer;

public abstract class AbstractAgent implements Agent {

    protected static final TBF tbf = new ProxyTBF(ImmutableThing.FACTORY);

    protected final Switchboard pbx;
    protected final Subject subject;

    protected AbstractAgent(Subject subject, Switchboard pbx) {
        this.pbx = pbx;
        this.subject = subject;
    }

    @Override
    public final Subject subject() {
        return subject;
    }

    protected void reply(Message request, String response) {
        var reply = new MessageImpl.Builder();
        reply.from(subject());
        reply.to(request.to());
        reply.content(response);
        reply.replyTo(request.id());
        pbx.post(reply);
    }

    protected boolean handle(Message message, String prefix, Consumer<String> consumer) {
        var content = message.content();
        if (!content.startsWith(prefix)) return false;

        var end = content.indexOf(' ');
        if (end == -1) return false;

        var postfix = content.substring(end + 1).trim();
        consumer.accept(postfix);
        return true;
    }

    protected boolean handle(Message message, String match, Runnable runnable) {
        var content = message.content();
        if (!content.equals(match)) return false;
        runnable.run();
        return true;
    }
}
