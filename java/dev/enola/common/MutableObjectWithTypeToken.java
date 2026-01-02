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
package dev.enola.common;

import static java.util.Objects.requireNonNull;

import com.google.common.reflect.TypeToken;

import java.util.Objects;

public final class MutableObjectWithTypeToken<T> implements ObjectWithTypeToken<T> {

    private final TypeToken<T> typeToken;
    private T object;

    public MutableObjectWithTypeToken(TypeToken<T> typeToken) {
        this.typeToken = requireNonNull(typeToken);
    }

    @Override
    public TypeToken<T> typeToken() {
        return typeToken;
    }

    public void set(ObjectWithTypeToken<?> objectWithTypeToken) {
        if (!objectWithTypeToken.typeToken().equals(typeToken)) {
            throw new IllegalArgumentException(
                    "Cannot set a " + objectWithTypeToken + " into a " + typeToken);
        }
        set(objectWithTypeToken, this);
    }

    @SuppressWarnings("unchecked")
    private void set(ObjectWithTypeToken<?> from, MutableObjectWithTypeToken<?> into) {
        var fromWithObject = (ObjectWithTypeToken<Object>) from;
        var intoWithObject = (MutableObjectWithTypeToken<Object>) into;
        intoWithObject.setObject(fromWithObject.object());
    }

    public void setObject(T object) {
        if (!typeToken.getRawType().isInstance(object)) {
            throw new IllegalArgumentException(
                    typeToken.toString() + " expected, but got: " + object.toString());
        }
        this.object = requireNonNull(object);
    }

    @Override
    public T object() {
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MutableObjectWithTypeToken<?> that)) return false;
        return Objects.equals(typeToken, that.typeToken) && Objects.equals(object, that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeToken, object);
    }

    @Override
    public String toString() {
        return "MutableObjectWithTypeToken{"
                + "typeToken="
                + typeToken
                + ", object="
                + object
                + '}';
    }
}
