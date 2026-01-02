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
 * PronounceableText is a {@link LangString} plus a phonetic representation.
 *
 * <p>For example, the English word "hello" would be represented as "hello" and "həˈləʊ".
 *
 * <p>This allows it to be correctly pronounced by a text-to-speech synthesis engine, such as e.g.
 * the (venerable!) <a href="https://espeak.sourceforge.net">eSpeak</a> (try it e.g. on <a
 * href="https://itinerarium.github.io/phoneme-synthesis/">itinerarium's phoneme-synthesis</a>), or
 * <a href="https://cloud.google.com/text-to-speech">Google Cloud Text-to-Speech AI</a> or <a
 * href="https://aws.amazon.com/polly/">Amazon Polly</a> (try it e.g. on <a
 * href="https://ipa-reader.com">ipa-reader.com</a>), etc.
 *
 * <p>Both the idea and the specific class name <code>PronounceableText</code> were directly
 * inspired by the <a
 * href="https://schema.org/PronounceableText">https://schema.org/PronounceableText</a>.
 */
public final class PronounceableText extends LangString {

    private final String phonetic;
    private final SpeechMarkupForm phoneticForm;

    private PronounceableText(
            String text, Locale language, String phonetic, SpeechMarkupForm phoneticForm) {
        super(text, language);
        this.phoneticForm = requireNonNull(phoneticForm, "phoneticForm");
        this.phonetic = requireNonNull(phonetic, "phonetic");
    }

    public static PronounceableText of(
            String text, Locale language, String phonetic, SpeechMarkupForm phoneticForm) {
        return new PronounceableText(text, language, phonetic, phoneticForm);
    }

    public String phonetic() {
        return phonetic;
    }

    public SpeechMarkupForm phoneticForm() {
        return phoneticForm;
    }

    @Override
    public String toString() {
        return super.toString() + "/" + phoneticForm + ":" + phonetic;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + phonetic.hashCode();
        result = 31 * result + phoneticForm.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PronounceableText that = (PronounceableText) o;
        return phonetic.equals(that.phonetic) && phoneticForm == that.phoneticForm;
    }
}
