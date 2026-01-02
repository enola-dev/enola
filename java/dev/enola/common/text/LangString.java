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

/**
 * LangString is a {@link String} plus its (mandatory) language.
 *
 * <p>For example, <i>"hello, world" in English</i> is a LangString.
 *
 * <p>For Text with Direction, use {@link DirectionalLangString}.
 *
 * <p>The class name <code>LangString</code> was chosen both because of its <i>*String</i> suffix
 * familiarity for Java developers, and inspired by <code>rdf:langString</code> (even though this
 * Java type is otherwise technically not specific to the Resource Description Framework [RDF] as
 * such).
 *
 * <p>Alternative names for the same concept include <code>LocalizedText</code>, <code>LanguageText
 * </code>, <code>MultilingualString</code> ('MLS'; or <code>MultiLangString</code> or <code>
 * MultiLanguageString</code>), <code>InternationalizedString</code> or <code>LanguageString</code>.
 */
public sealed class LangString permits DirectionalLangString, PronounceableText {

    private final String text;
    private final Locale language;

    LangString(String text, Locale language) {
        this.text = requireNonNull(text, "text");
        this.language = requireNonNull(language, "language");
    }

    public static LangString of(String text, Locale language) {
        return new LangString(text, language);
    }

    public static LangString of(String text, Locale language, Direction direction) {
        return new DirectionalLangString(text, language, direction);
    }

    public String text() {
        return text;
    }

    public Locale language() {
        return language;
    }

    public Direction direction() {
        return Direction.AUTO;
    }

    @Override
    public String toString() {
        return "\"" + text + "\"@" + language.toLanguageTag();
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + language.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        LangString that = (LangString) o;
        return text.equals(that.text) && language.equals(that.language);
    }
}
