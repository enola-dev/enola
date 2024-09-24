/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.testlib;

import dev.enola.thing.repo.ThingsBuilder;
import dev.enola.thing.repo.ThingsRepository;

import org.junit.Test;

public class ThingsSubjectTest {

    @Test
    public void empty() {
        ThingsRepository r = new ThingsBuilder();
        ThingsSubject.assertThat(r).isEqualTo("classpath:/empty.yaml");
    }

    @Test
    public void ttl() {
        ThingsBuilder r = new ThingsBuilder();
        r.getBuilder("https://example.org/greeting1")
                .set("https://example.org/message", "hello, world");
        ThingsSubject.assertThat(r).isEqualTo("classpath:/greeting1.ttl");
    }
}
