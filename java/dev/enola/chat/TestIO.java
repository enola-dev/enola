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
package dev.enola.chat;

import dev.enola.common.linereader.IO;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TestIO implements IO {

    private int line = 0;
    private final List<String> input;
    private final List<String> output = new ArrayList<>();

    public TestIO(List<String> lines) {
        this.input = lines;
    }

    @Override
    public @Nullable String readLine() {
        if (line < input.size()) return input.get(line++);
        else return null;
    }

    @Override
    public @Nullable String readLine(String prompt) {
        printf(prompt);
        return readLine();
    }

    @Override
    public void printf(String format, Object... args) {
        output.add(String.format(format, args));
    }

    public List<String> getOutput() {
        return output;
    }
}
