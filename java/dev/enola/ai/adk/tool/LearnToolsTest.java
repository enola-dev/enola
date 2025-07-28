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
package dev.enola.ai.adk.tool;

import static com.google.common.truth.Truth.assertThat;

import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.FunctionTool;

import org.junit.Test;

import java.util.Map;

public class LearnToolsTest {

    // TODO Remove this again?

    @Schema(name = "Current Time", description = "Returns the current time in the given city")
    public static Map<String, Object> getCurrentTime(
            @Schema(description = "The name of the city for which to retrieve the current time")
                    String city) {
        return Map.of("time", "12:00");
    }

    @Test
    public void test() {
        var tool = FunctionTool.create(LearnToolsTest.class, "getCurrentTime");
        assertThat(tool.name()).isEqualTo("Current Time");
        assertThat(tool.description()).isEqualTo("Returns the current time in the given city");
    }
}
