/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.format.tika;

import static com.google.common.truth.Truth.assertThat;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.junit.Test;

import java.io.IOException;

/** Test (and learn using) Tika itself - not its integration with Enola. */
public class TikaTest {

    @Test
    public void detectLongerExtension() throws IOException {
        var metadata = new Metadata();
        var tika = new DefaultDetector();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, "file:///test.warc.gz");
        assertThat(tika.detect(null, metadata)).isEqualTo(new MediaType("application", "warc+gz"));
    }
}
