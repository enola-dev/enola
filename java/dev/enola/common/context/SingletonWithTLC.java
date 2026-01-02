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
abstract class SingletonWithTLC<T> extends Singleton<T> {

    // TODO This was a previous version of class Singleton - keep, or now remove?

    private final Class<T> klass;

    protected SingletonWithTLC(Class<T> klass) {
        this.klass = klass;
    }

    @Override
    public T get() {
        // TODO Rethink if this should FIRST check the Singleton and THEN the TLC, or the other way?
        if (!isSet()) {
            var opt = TLC.optional(klass);
            if (opt.isPresent()) return opt.get();
            throw new IllegalStateException();
        } else return super.get();
    }

    // NOT public void reset() { this.value = null; }
}
