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
package dev.enola.audio.voice.twilio.relay;

import com.google.common.base.Strings;
import com.twilio.security.RequestValidator;

import dev.enola.common.context.TestContext;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.secret.SecretManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class SignatureValidator {

    private static final Logger logger = LoggerFactory.getLogger(SignatureValidator.class);

    private final RequestValidator validator;

    public SignatureValidator(SecretManager secretManager)
            throws IllegalStateException, IOException {
        var token =
                TestContext.isUnderTest()
                        ? "TESTING"
                        : secretManager.get("TWILIO_AUTH_TOKEN").map(String::new);
        this.validator = new RequestValidator(token);
    }

    public boolean validate(String fullURL, String expectedSignature) {
        if ("TRUE".equals(System.getenv("TWILIO_SKIP_AUTH"))) {
            logger.warn("Skipping Twilio signature validation! :(");
            return true;
        }
        if (TestContext.isUnderTest()) return true;
        if (Strings.isNullOrEmpty(expectedSignature)) {
            logger.warn("Missing Twilio signature");
            return false;
        }

        var urlWithoutParameters = URIs.getPath(fullURL);
        var parameters = URIs.getQueryMap(fullURL);

        var isValid = validator.validate(urlWithoutParameters, parameters, expectedSignature);
        if (!isValid) {
            logger.warn(
                    "Invalid Twilio signature: fullURL={}, urlWithoutParameters={}, parameters={},"
                            + " expectedSignature={}",
                    fullURL,
                    urlWithoutParameters,
                    parameters,
                    expectedSignature);
        }
        return isValid;
    }
}
