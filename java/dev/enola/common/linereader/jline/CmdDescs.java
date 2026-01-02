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

import com.google.common.collect.ImmutableMap;

import org.jline.console.CmdDesc;
import org.jline.console.CommandRegistry;

import java.util.List;

public class CmdDescs {

    public static ImmutableMap<String, CmdDesc> buildMap(CommandRegistry cmdRegistry) {
        var commandDescriptions = ImmutableMap.<String, CmdDesc>builder();
        for (var commandName : cmdRegistry.commandNames()) {
            commandDescriptions.put(
                    commandName, cmdRegistry.commandDescription(List.of(commandName)));
        }
        return commandDescriptions.build();
    }

    private CmdDescs() {}
}
