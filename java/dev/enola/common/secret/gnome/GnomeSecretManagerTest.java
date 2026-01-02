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
package dev.enola.common.secret.gnome;

import de.swiesend.secretservice.simple.SimpleCollection;

import org.junit.Test;

import java.io.IOException;

public class GnomeSecretManagerTest {

    @Test
    public void test() throws IOException {
        // NOTE: This is not working when running from Bazel (because env variables are not set)
        if (!SimpleCollection.isGnomeKeyringAvailable()) return;
        GnomeSecretManager.main(new String[0]);
    }
}
