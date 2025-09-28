/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 👟 Sneaker for (controlled) "sneaky Throw".
 *
 * <p>See e.g.
 * https://4comprehension.com/sneakily-throwing-exceptions-in-lambda-expressions-in-java/ and
 * https://www.baeldung.com/java-lambda-exceptions, but also
 * https://github.com/google/guava/wiki/Why-we-deprecated-Throwables.propagate, for some related
 * background. Contrary to what those and similar posts state, the way this is done here is actually
 * entirely "safe" in Java... note that the trick here, and important difference to a general 👟
 * Sneaker, is that the {@link MoreStreams#forEach(Stream, CheckedConsumer)} signature actually
 * declares the throws E - so it's safe!
 */
public final class Sneaker {

    @SuppressWarnings("unchecked")
    // This method should be kept private and never made public.
    // Sneaky throwing in general is not a good idea, but
    private static <T extends Exception, R> R sneakyThrow(Exception t) throws T {
        throw (T) t; // ( ͡° ͜ʖ ͡°)
    }

    // This method is intentionally only package local, as is this entire class.
    static <T, E extends Exception> Consumer<T> sneakyConsumer(
            CheckedConsumer<T, E> checkedConsumer) {
        return t -> {
            try {
                checkedConsumer.accept(t);
            } catch (Exception e) {
                sneakyThrow(e);
            }
        };
    }

    static <T, R, E extends Exception> Function<T, R> sneakyFunction(
            CheckedFunction<T, R, E> checkedFunction) {
        return t -> {
            try {
                return checkedFunction.apply(t);
            } catch (Exception e) {
                return sneakyThrow(e);
            }
        };
    }

    private Sneaker() {}
}
