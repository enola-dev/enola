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

import dev.enola.model.w3.rdfs.HasComment;
import dev.enola.model.w3.rdfs.HasLabel;
import dev.enola.thing.HasIRI;
import dev.enola.thing.java.HasType;

import java.security.PublicKey;
import java.util.Base64;

/**
 * Subject. Often a user (person) or an organization / group, but also a machine, or a service, etc.
 *
 * <p>It may be a "tangible" real-world entity, or an "intangible" virtual entity.
 *
 * <p>Could be "Alice", or "Bob" - or... YOU!
 */
// TODO HasPublicKeys ?
public interface Subject extends HasIRI, HasType, HasLabel, HasComment {

    // TODO HasAuthPublicKeys

    // TODO HasNickname String nickName(); Set<String> nickNameAliases();

    // skipcq: JAVA-E0169
    interface Builder
            extends HasType.Builder<Subject>,
                    HasLabel.Builder<Subject>,
                    HasComment.Builder<Subject> {
        /* TODO HasIRI.Builder<Builder>, */

        @Override
        Builder iri(String iri);

        // TODO Move to HasPublicKeys interface
        default Builder addAuthPubKey(PublicKey pubKey) {
            // TODO Check Decentralized IDs (DID) for an existing "publicKey" IRI
            // TODO Use
            // https://github.com/filip26/copper-multicodec/blob/05c14b5068078d9cd522de374abbfc76f66f605b/src/main/java/com/apicatalog/multicodec/codec/KeyCodec.java#L118
            var bytes = Base64.getEncoder().encodeToString(pubKey.getEncoded());
            // TODO Document https://enola.dev/authPubKeys (extend https://schema.org/identifier)
            //   auth* as opposed to a Subject's (current) cryptPubKey
            var pubKeyText = pubKey.getAlgorithm() + ":" + pubKey.getFormat() + ":" + bytes;
            add("https://enola.dev/authPubKeys", pubKeyText);
            return this;
        }

        // TODO Builder nickname(String nickname) {

        @Override
        default Builder label(String label) {
            HasLabel.Builder.super.label(label);
            return this;
        }

        @Override
        default Builder comment(String label) {
            HasComment.Builder.super.comment(label);
            return this;
        }

        Subject build();
    }
}
