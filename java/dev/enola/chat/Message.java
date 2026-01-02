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
package dev.enola.chat;

import dev.enola.identity.Subject;

import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

/** Message. AKA "Post" or "Comment" or "Tweet" - or even an "Email". */
public interface Message { // TODO extends Thing

    Object id();

    Optional<Object> replyTo();

    Subject from();

    Room to();

    Instant createdAt();

    Instant modifiedAt();

    // TODO Add Locale language(), by introducing a LangString-like type with format datatype?

    // TODO Attachments!! Useful both for Emails, as well as for LLMs...
    // TODO Instead of attachments, add "Parts", like a Multipart MIME message - and in A2A!
    String content();

    // TODO Replace enum Format with MediaType
    Format format();

    enum Format {
        /** Plain Text, without formatting. */
        PLAIN,

        /**
         * Markdown format.
         *
         * <p>Markdown could be converted to ANSI for Terminal Shell output e.g. using <a
         * href="https://github.com/swsnr/mdcat">mdcat</a>.
         */
        MARKDOWN,

        /**
         * HTML format.
         *
         * <p>HTML could be converted to ANSI for Terminal Shell output e.g. using <a
         * href="https://en.m.wikipedia.org/wiki/W3m">w3m</a>.
         */
        HTML,

        /**
         * "ANSI" format, as in "Terminal text", with "ANSI escape &amp; color etc. control codes".
         *
         * <p>Should typically be rendered in a fixed width (monospaced) font.
         */
        ANSI
    }

    // TODO Signature by from Subject!

    interface Builder {

        Builder id(Object id);

        Builder replyTo(Object replyToMessageID);

        @Nullable Object id();

        Builder from(Subject from);

        @Nullable Subject from();

        Builder to(Room to);

        Builder createdAt(Instant createdAt);

        @Nullable Instant createdAt();

        Builder modifiedAt(Instant modifiedAt);

        @Nullable Instant modifiedAt();

        Builder subject(String subject);

        Builder content(String content);

        Builder format(Format format);

        Message build();
    }
}
