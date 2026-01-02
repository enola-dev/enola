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
package dev.enola.common.time;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimesTest {

    @Test
    public void beginningOfTimeInstantToString() {
        var instant = Instant.MIN.plus(1, ChronoUnit.DAYS);
        var text = "-1000000000-01-02T00:00:00Z";
        assertThat(instant.toString()).isEqualTo(text);
    }

    @Test
    public void endOfTimeWithInstantParse() {
        // NB: The + prefix is mandatory if it's not 4 YYYY digits
        Instant.parse("+300000-12-30T23:59:59Z");
    }
}
