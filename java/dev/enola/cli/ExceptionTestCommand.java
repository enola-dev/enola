/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.cli;

import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        hidden = true,
        name = "test-exception",
        description = "Used only to test that exception handling works as expected")
public class ExceptionTestCommand implements Runnable {

    @Override
    public void run() {
        throw new RuntimeException("Test Exception", new IOException("Test I/O failure"));
    }
}
