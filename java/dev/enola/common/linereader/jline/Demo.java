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
package dev.enola.common.linereader.jline;

import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.TerminalBuilder;

/** Demo example main() of JLineIO; without any Chat, Shell, LLM, etc. */
public class Demo {

    public static void main(String[] args) throws Exception {
        try (var terminal = TerminalBuilder.terminal()) {
            var consumer = new JLineBuiltinCommandsProcessor(terminal);
            var tailTips = consumer.commandDescriptions();
            try (var jLineIO =
                    new JLineIO(
                            System.getenv(),
                            terminal,
                            new DefaultParser(),
                            consumer.completer(),
                            tailTips,
                            null,
                            true)) {
                consumer.lineReader(jLineIO.lineReader());

                jLineIO.printf("hello, world\n");
                do {
                    var input = jLineIO.readLine("> ");
                    if (input == null || input.isEmpty()) break;
                    consumer.accept(input);

                } while (true);
            }
        }
    }
}
