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
package dev.enola.common.io.mediatype;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

class MediaTypeProvidersTest {

    @Test
    void empty() {
        var mtp = new MediaTypeProviders();
        assertThat(mtp.normalize(TestMediaType.TEST_ALTERNATIVE))
                .isEqualTo(TestMediaType.TEST_ALTERNATIVE);
        assertThat(mtp.extensionsToTypes()).isEmpty();
        assertThat(mtp.knownTypesWithAlternatives()).isEmpty();
    }

    @Test
    void testMediaType() {
        var mtp = new MediaTypeProviders(new StandardMediaTypes(), new TestMediaType());
        assertThat(mtp.normalize(TestMediaType.TEST_ALTERNATIVE)).isEqualTo(TestMediaType.TEST);
        assertThat(mtp.extensionsToTypes()).containsEntry(".test", TestMediaType.TEST);
        assertThat(mtp.knownTypesWithAlternatives())
                .containsEntry(TestMediaType.TEST, newHashSet(TestMediaType.TEST_ALTERNATIVE));
    }
}
