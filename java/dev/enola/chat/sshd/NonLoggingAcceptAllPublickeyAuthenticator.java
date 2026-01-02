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
package dev.enola.chat.sshd;

import org.apache.sshd.common.AttributeRepository.AttributeKey;
import org.apache.sshd.server.auth.pubkey.StaticPublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;

class NonLoggingAcceptAllPublickeyAuthenticator extends StaticPublickeyAuthenticator {

    private static final AttributeKey<PublicKey> USER_KEY = new AttributeKey<>();

    NonLoggingAcceptAllPublickeyAuthenticator() {
        super(true);
    }

    @Override
    protected void handleAcceptance(String username, PublicKey key, ServerSession session) {
        // Do not delegate to super() - to avoid WARN log; instead:
        // This assumes that CoreModuleProperties.AUTH_METHODS is set to "publickey",
        // and will not work e.g. for (x2) "publickey,publickey" or "publickey,password" etc.
        var existing = session.getAttribute(USER_KEY);
        if (existing != null && !existing.equals(key)) {
            throw new IllegalStateException("Session already accepted");
        }
        session.setAttribute(USER_KEY, key);
    }

    public static PublicKey getPublicKey(ServerSession session) {
        var key = session.getAttribute(USER_KEY);
        if (key == null)
            throw new IllegalStateException("Session never accepted (or already got its Key?!)");
        return key;
    }
}
