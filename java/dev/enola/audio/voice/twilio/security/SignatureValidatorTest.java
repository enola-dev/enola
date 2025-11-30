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
package dev.enola.audio.voice.twilio.security;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.context.TestContext;
import dev.enola.common.secret.InMemorySecretManager;
import dev.enola.common.secret.SecretManager;

import org.junit.Test;

public class SignatureValidatorTest {

    // This secret token is just for testing, it was long ago invalidated on the Twilio Console
    String token = "c0e4d9d4fff66d194f3101ccd7c3a38c";
    SecretManager sm = new InMemorySecretManager("TWILIO_AUTH_TOKEN", token);

    // Cryptographic Signatures are never "secret"
    String signature = "CSS4F/5/iw8S6Zkm3aULeNJaVC8=";

    String GOOD_URL = "wss://70cd1a93aa44.ngrok-free.app/";
    String BAD_URL = "wss://70cd1a93aa44.ngrok-free.app";

    @Test
    public void noTestContext() {
        assertThat(TestContext.isUnderTest()).isFalse();
    }

    @Test
    public void validSignature() throws Exception {
        var validator = new SignatureValidator(sm);
        assertThat(validator.validate(GOOD_URL, signature)).isTrue();
    }

    @Test
    public void invalidSignature() throws Exception {
        var validator = new SignatureValidator(sm);
        assertThat(validator.validate(BAD_URL, signature)).isFalse();
    }
}
