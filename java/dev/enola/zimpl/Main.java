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
package dev.enola.zimpl;

import dev.enola.Enola;
import dev.enola.model.enola.action.Get;

/**
 * Enola CLI demo MVP.
 *
 * <p>The "real" CLI is in the <code>dev.enola.cli</code> package.
 */
public class Main {

    public static void main(String[] args) {
        System.exit(execute(args));
    }

    private static int execute(String[] args) {
        String actionIRI;
        Enola enola = new EnolaProvider().get();
        if (args.length == 0 || args.length > 2) {
            // TODO Enter an interactive REPL loop instead...
            System.err.println("Usage: enola <ObjectIRI> [ActionIRI]");
            return -1;
        } else if (args.length == 1) {
            actionIRI = Get.IRI;
        } else {
            actionIRI = args[1];
        }
        enola.act(args[0], actionIRI);
        // TODO Support longer | piped chains...
        return 0;
    }
}
