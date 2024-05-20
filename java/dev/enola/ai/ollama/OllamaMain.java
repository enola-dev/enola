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
package dev.enola.ai.ollama;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

public class OllamaMain {

    // See https://github.com/vorburger/vorburger.ch-Notes/blob/develop/ml/ollama1.md

    public static void main(String[] args)
            throws IOException, OllamaBaseException, URISyntaxException, InterruptedException {
        if (args.length != 1) {
            System.err.println("Usage: java OllamaMain <path-to-directory-of-code>");

            var modelName = "gemma:7b"; // TODO "codegemma:7b"
            var host = "http://localhost:11434/";
            var options = new OptionsBuilder().setSeed(1).build();

            OllamaAPI ollamaAPI = new OllamaAPI(host);
            ollamaAPI.setRequestTimeoutSeconds(7);
            ollamaAPI.ping();
            // System.out.println(ollamaAPI.getModelDetails(modelName));
            System.out.println(
                    ollamaAPI
                            .generate(
                                    modelName,
                                    "Hello, the sky is grey today. Hi, what's sky's colour? Do not"
                                            + " provide any reasoning, just answer the question",
                                    options)
                            .getResponse());
        }
    }
}
