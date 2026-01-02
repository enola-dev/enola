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
package dev.enola.ai.adk.core;

import com.google.adk.agents.BaseAgent;
import com.google.adk.events.Event;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import io.reactivex.rxjava3.core.Flowable;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CLI {

    // TODO Write JLineIO alternative; see Chat2Command#ai (in package dev.enola.ai.adk.jline)

    public static void main(String[] args, BaseAgent agent) {
        try (var runner = new UserSessionRunner(userID(), agent)) {
            if (args.length > 0) run(runner, args);
            else repl(runner);
        }
    }

    // TODO Share code here with dev.enola.cli.AiCommand
    private static void run(UserSessionRunner runner, String[] args) {
        var prompt = String.join(" ", args);
        run(runner, prompt);
    }

    // TODO Share code here with dev.enola.cli.Chat2Command
    private static void repl(UserSessionRunner runner) {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (true) {
                System.out.print("\nYou > ");
                String userInput = scanner.nextLine();

                if ("quit".equalsIgnoreCase(userInput)) {
                    break;
                }

                System.out.print("\nAgent > ");
                run(runner, userInput);
            }
        }
    }

    private static void run(UserSessionRunner runner, String userInput) {
        Content userMsg = Content.fromParts(Part.fromText(userInput));
        Flowable<Event> events = runner.runAsync(userMsg);
        // TODO Don't print function calls, but log them; only print the actual responses.
        events.blockingForEach(event -> System.out.println(toString(event) + "\n"));
    }

    private static String toString(Event event) {
        // event.stringifyContent()
        // TODO Skip empty fields!
        return event.toJson();
    }

    public static String userID() {
        String userID = System.getProperty("user.name");
        if (userID == null) userID = "CLI";
        return userID;
    }
}
