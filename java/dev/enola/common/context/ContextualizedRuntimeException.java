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
package dev.enola.common.context;

import org.jspecify.annotations.Nullable;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * {@link RuntimeException} with {@link Context}.
 *
 * @see ContextualizedException
 */
public class ContextualizedRuntimeException extends RuntimeException {

    private final @Nullable Context context;

    public ContextualizedRuntimeException(String message) {
        super(message);
        context = TLC.get();
    }

    public ContextualizedRuntimeException(String message, Throwable cause) {
        super(message, cause);
        context = TLC.get();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if (context != null) {
            if (context.last != null) s.println("Context:");
            context.append(s, ContextualizedException.INDENT);
            s.flush();
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (context != null) {
            if (context.last != null) s.println("Context:");
            context.append(s, ContextualizedException.INDENT);
            s.flush();
        }
    }
}
