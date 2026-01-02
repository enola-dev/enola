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
package dev.enola.common;

import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public final class SuccessOrError<T> {

    public static <T> SuccessOrError<T> success(T success) {
        return new SuccessOrError<T>(success, null);
    }

    public static <T> SuccessOrError<T> error(String errorMessage) {
        return new SuccessOrError<T>(null, errorMessage);
    }

    private final @Nullable T success;
    private final @Nullable String errorMessage;

    private SuccessOrError(@Nullable T success, @Nullable String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
        if (success == null && errorMessage == null) throw new IllegalArgumentException();
    }

    public <X> X map(Function<T, X> successFunction, Function<String, X> errorFunction) {
        if (success != null) return successFunction.apply(success);
        else if (errorMessage != null) return errorFunction.apply(errorMessage);
        throw new IllegalStateException();
    }
}
