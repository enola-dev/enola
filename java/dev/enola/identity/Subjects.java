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
package dev.enola.identity;

import com.google.common.annotations.VisibleForTesting;

import dev.enola.data.id.UUID_IRI;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;

import java.security.PublicKey;

public class Subjects {

    private final TBF tbf;
    private final Subject local;

    public Subjects() {
        this(new ProxyTBF(ImmutableThing.FACTORY));
    }

    public Subjects(TBF tbf) {
        this.tbf = tbf;

        String hostname = Hostnames.LOCAL;
        String userName = System.getProperty("user.name");
        String userLabel = userName + "@" + hostname;

        this.local =
                tbf.create(Subject.Builder.class, Subject.class)
                        .iri("subject://" + hostname + "/" + userName)
                        .label(userLabel)
                        .build();
    }

    public Subject local() {
        return local;
    }

    /** <a href="https://en.wikipedia.org/wiki/Alice_and_Bob">Alice</a>. */
    public @VisibleForTesting Subject alice() {
        Subject.Builder sb = tbf.create(Subject.Builder.class, Subject.class);
        return sb.iri("https://example.com/alice").label("Alice").build();
    }

    public Subject fromPublicKey(PublicKey pubKey, String username) {
        // TODO Store & re-lookup Subject by pubKey in Thing Store...
        Subject.Builder sb = tbf.create(Subject.Builder.class, Subject.class);
        return sb.iri(new UUID_IRI().toString()).addAuthPubKey(pubKey).label(username).build();
    }
}
