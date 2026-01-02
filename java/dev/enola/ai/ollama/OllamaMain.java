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
package dev.enola.ai.ollama;

import static dev.enola.common.function.MoreStreams.forEach;

import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.io.resource.stream.GlobResolver;
import dev.enola.common.io.resource.stream.GlobResolvers;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.utils.OptionsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

public class OllamaMain {

    // See https://github.com/vorburger/vorburger.ch-Notes/blob/develop/ml/ollama1.md

    public static void main(String[] args)
            throws IOException, OllamaBaseException, URISyntaxException, InterruptedException {
        // This won't work, because it's too big:
        // args = new String[]
        // {"file:/home/vorburger/git/github.com/enola-dev/enola/java/**.java?charset=UTF-8",
        // "What's this code all about?"};
        // ...
        // But e.g. "file:$PWD/java/dev/enola/common/function/**.java?charset=UTF-8" kinda works!

        if (args.length != 2) {
            System.err.println("Usage: java OllamaMain <path-to-directory-of-code> <prompt>");
            System.exit(1);
        }

        var context = suckFiles(args[0]);
        var prompt = args[1];

        var modelName = "codegemma:7b";
        var baseURL = "http://localhost:11434/";
        var options = new OptionsBuilder().setSeed(1).build();

        OllamaAPI ollamaAPI = new OllamaAPI(baseURL);
        ollamaAPI.setRequestTimeoutSeconds(30);
        ollamaAPI.ping();
        // System.out.println(ollamaAPI.getModelDetails(modelName));

        System.out.println("Context Size is: " + context.length());
        System.out.println(
                ollamaAPI
                        .generate(
                                modelName,
                                "Here is a lot of source code: "
                                        + context
                                        + "\n"
                                        + "Now please answer the following question about this"
                                        + " code, and avoid repeating yourself: "
                                        + prompt,
                                // "Do not provide any reasoning, just answer the question",
                                true,
                                options)
                        .getResponse());
        System.out.println("Context Size was: " + context.length());
    }

    private static String suckFiles(String globURI) throws IOException {
        var sb = new StringBuilder();
        var rps = new ResourceProviders();
        GlobResolver globResolver = new GlobResolvers();
        try (var resources = globResolver.get(globURI)) {
            forEach(resources, uri -> sb.append(rps.getResource(uri).charSource().read()));
        }
        return sb.toString();
    }
}
