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
package dev.enola.model.enola.bookmark;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.resource.ClasspathResource;

import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class DeliciousTest {

    @Test
    public void readJSON() throws IOException {
        var EXPECTED =
                new Delicious(
                        "Pandoc",
                        List.of("documentation", "markup", "conversion"),
                        "http://pandoc.org",
                        "Pandoc is a Markdown etc. converter.",
                        Instant.parse("2016-05-18T06:51:25Z"),
                        75L,
                        "maciej",
                        "0");

        var resource = new ClasspathResource("delicious.json");
        var reader = new DeliciousReader();
        var deliciouss = reader.readJSON(resource);
        assertThat(deliciouss).hasSize(3);
        assertThat(deliciouss).contains(EXPECTED);
    }
}
