/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.chat.sshd;

import org.apache.sshd.server.auth.pubkey.StaticPublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class NonLoggingAcceptAllPublickeyAuthenticator extends StaticPublickeyAuthenticator {

    // TODO Isn't there a better way to do this?! See
    //   https://github.com/apache/mina-sshd/issues/748...

    private final Map<ServerSession, PublicKey> keys = new ConcurrentHashMap<>();

    NonLoggingAcceptAllPublickeyAuthenticator() {
        super(true);
    }

    @Override
    protected void handleAcceptance(String username, PublicKey key, ServerSession session) {
        // Do not delegate to super() - to avoid WARN log; instead:
        var existing = keys.putIfAbsent(session, key);
        if (existing != null && !existing.equals(key)) {
            throw new IllegalStateException("Session already accepted");
        }
    }

    public PublicKey getPublicKey(ServerSession session) {
        var key = getAndRemove(session);
        if (key == null)
            throw new IllegalStateException("Session never accepted (or already got its Key?!)");
        return key;
    }

    private PublicKey getAndRemove(ServerSession session) {
        final PublicKey[] removedValue = new PublicKey[1];
        keys.computeIfPresent(
                session,
                (key, value) -> {
                    // 'value' is the current value associated with 'key', keep it:
                    removedValue[0] = value;
                    return null; // Returning null removes the entry
                });
        return removedValue[0];
    }
}
