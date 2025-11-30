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

import com.google.common.base.Strings;
import com.twilio.security.RequestValidator;

import dev.enola.common.context.TestContext;
import dev.enola.common.secret.SecretManager;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public final class SignatureValidator {

    private static final Logger logger = LoggerFactory.getLogger(SignatureValidator.class);

    private final RequestValidator validator;

    public SignatureValidator(SecretManager secretManager)
            throws IllegalStateException, IOException {
        String token =
                TestContext.isUnderTest()
                        ? "TESTING"
                        : secretManager.get("TWILIO_AUTH_TOKEN").map(String::new);
        this.validator = new RequestValidator(token);
    }

    public boolean validate(final String externalURL, @Nullable final String expectedSignature) {
        if ("TRUE".equals(System.getenv("TWILIO_SKIP_AUTH"))) {
            logger.warn("Skipping Twilio signature validation! :(");
            return true;
        }
        if (TestContext.isUnderTest()) return true;
        if (Strings.isNullOrEmpty(expectedSignature)) {
            logger.warn("Missing Twilio signature");
            return false;
        }
        if (Strings.isNullOrEmpty(externalURL)) {
            logger.warn("Missing [null] external URL?!");
            return false;
        }

        var isValid = validator.validate(externalURL, Map.of(), expectedSignature);
        if (!isValid)
            logger.warn("Invalid Twilio signature: {} for {}", expectedSignature, externalURL);
        return isValid;
    }

    public boolean validate(
            @Nullable final String hostHeader,
            @Nullable final String forwardedHostsHeader,
            @Nullable String path,
            @Nullable final String expectedSignature) {
        String hostname = null;
        if (!Strings.isNullOrEmpty(forwardedHostsHeader)) {
            hostname = forwardedHostsHeader;
            // The X-Forwarded-Host header can be a comma-separated list of hosts;
            // the first one is the original host.
            int commaIndex = hostname.indexOf(',');
            if (commaIndex != -1) {
                hostname = hostname.substring(0, commaIndex);
            }
            hostname = hostname.trim();
        } else {
            hostname = hostHeader;
        }
        if (Strings.isNullOrEmpty(hostname)) {
            return false;
        }
        if (Objects.isNull(path)) {
            path = "";
        }

        var url = "wss://" + hostname + path;
        return validate(url, expectedSignature);
    }
}
