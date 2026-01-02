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
package dev.enola.common.io.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExamplePlainClass {

    @Nullable public String string;

    public final Set<String> stringSet = new HashSet<>();

    public final List<String> stringList = new ArrayList<>();

    public @Nullable ExampleRecord example;

    public @JsonProperty("default") String defaultValue;

    public @Nullable Instant timestamp;

    @JsonProperty("private")
    public boolean isPrivate;
}
