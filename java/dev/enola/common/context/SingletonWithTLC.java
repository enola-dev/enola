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
package dev.enola.common.context;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Singleton.
 *
 * <p>Singletons statically hold a value for the lifetime of the JVM.
 *
 * <p>This is great for "global" services; some initialization code called at the start of an
 * application sets its value, which can then be easily looked up from anywhere in an application.
 * It is not possible to later change or reset that value - this is an intentional design decision.
 *
 * <p>The problem with that is that this is not very suitable for tests, where there typically is
 * not a clear single entry point. That's why this is integrated with {@link TLC}, and returns an
 * instance of its type pushed on the Context - if there is one.
 *
 * <p>See also SingleTest for how to use this. TODO Provide 2 inline code examples (main() + Test).
 */
abstract class SingletonWithTLC<T> implements Supplier<T> {

    // TODO This was a previous version of class Singleton - keep, or now remove?

    private final Class<T> klass;
    private @Nullable T value;

    protected SingletonWithTLC(Class<T> klass) {
        this.klass = klass;
    }

    public void set(T value) {
        if (this.value != null) throw new IllegalStateException();
        if (value == null) throw new IllegalArgumentException();
        this.value = value;
    }

    @Override
    public T get() {
        if (value == null) {
            var opt = TLC.optional(klass);
            if (opt.isPresent()) return opt.get();
            throw new IllegalStateException();
        } else return value;
    }

    // NOT public void reset() { this.value = null; }
}
