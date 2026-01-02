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
package dev.enola.common.text;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

final class DirectionalLangString extends LangString {
    private final Direction direction;

    DirectionalLangString(String text, Locale language, Direction direction) {
        super(text, language);
        this.direction = requireNonNull(direction, "direction");
    }

    @Override
    public Direction direction() {
        return direction;
    }

    @Override
    public String toString() {
        return super.toString() + "-" + direction;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + direction.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DirectionalLangString that = (DirectionalLangString) o;
        return direction == that.direction;
    }
}
