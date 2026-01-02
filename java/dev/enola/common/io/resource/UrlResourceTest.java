/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.resource;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.io.Resources;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class UrlResourceTest {

    // NB: ClasspathResourceTest (for ClasspathResource, which extends UrlResource) covers basics.

    // TODO Implement, using
    // https://docs.oracle.com/en/java/javase/17/docs/api/jdk.httpserver/module-summary.html ?

    // TODO Test that an URL can return MediaType HTML without have a .htm file extension

    @Test
    public void testJarScheme() throws IOException, URISyntaxException {
        var url = Resources.getResource("test-emoji.txt").toURI();
        var rp = new ResourceProviders(new UrlResource.Provider(UrlResource.Scheme.jar));
        var emoji = rp.getReadableResource(url).charSource().read();
        assertThat(emoji).isEqualTo("🕵🏾‍♀️\n");
    }
}
