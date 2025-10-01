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
package dev.enola.cli;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.adk.events.Event;
import com.google.common.collect.ImmutableList;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.enola.ai.adk.core.CLI;
import dev.enola.ai.adk.core.UserSessionRunner;
import dev.enola.cli.AiOptions.WithAgentName;
import dev.enola.common.context.TLC;

import io.reactivex.rxjava3.core.Flowable;

import org.jspecify.annotations.Nullable;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.net.URI;

@CommandLine.Command(name = "ai", description = "Run AI, print response (and then exit)")
public class AiCommand extends CommandWithResourceProvider {

    @Spec CommandSpec spec;

    @CommandLine.ArgGroup(exclusive = false)
    @Nullable WithAgentName aiOptions;

    @CommandLine.Option(
            names = {"--inURL"},
            description = "URL to Input (e.g. prompt.txt)")
    @Nullable URI promptURL;

    @CommandLine.Option(
            names = {"--in"},
            description = "Text Input (e.g. 'hello, world')")
    @Nullable String prompt;

    @CommandLine.Option(
            names = {"--attach"},
            description = "URL to file to attach (e.g. image.png)")
    @Nullable URI[] attachments;

    // TODO Input? For consistency, check other commands...

    @Override
    public void run() throws Exception {
        super.run();
        try (var ctx = TLC.open()) {
            setup(ctx);
            runInContext();
        }
    }

    private void runInContext() throws IOException {
        var out = spec.commandLine().getOut();

        if (isNullOrEmpty(prompt))
            if (promptURL != null) prompt = rp.getNonNull(promptURL).charSource().read();
            else throw new IllegalArgumentException("No prompt; use --in or --inURL");

        var agent = AI.load1(rp, aiOptions);
        try (var runner = new UserSessionRunner(CLI.userID(), agent)) {

            // TODO Share code here with dev.enola.ai.adk.core.CLI#run()

            var partsBuilder = ImmutableList.<Part>builder();
            partsBuilder.add(Part.fromText(prompt));
            
            if (attachments != null) {
                for (var attachmentURI : attachments) {
                    var resource = rp.getNonNull(attachmentURI);
                    var mediaType = resource.mediaType().toString();
                    partsBuilder.add(Part.fromUri(attachmentURI.toString(), mediaType));
                }
            }

            Content userMsg = Content.fromParts(partsBuilder.build());
            Flowable<Event> eventsFlow = runner.runAsync(userMsg);

            eventsFlow.blockingSubscribe(
                    event -> {
                        // TODO stringifyContent() needs to be improved...
                        //   Only output for user should be printed, tool calls etc. must be logged!
                        //   see also Chat2Command!
                        out.println(event.stringifyContent());
                    },
                    e -> {
                        throw e;
                    });
        }
    }
}
