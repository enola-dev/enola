/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

/** Message. AKA "Post" or "Comment" or "Tweet" - or even an "Email". */
public interface Message {

    Object id();

    Subject from();

    Room to();

    Instant createdAt();

    Instant modifiedAt();

    // TODO Add Locale language(), by introducing a LangString-like type with format datatype?

    String content();

    Format format();

    enum Format {
        PLAIN,
        MARKDOWN,
        HTML,
    }

    // TODO Attachments!! Useful both for Emails, as well as for LLMs...

    // TODO Signature by from Subject!

    interface Builder {

        Builder id(Object id);

        @Nullable Object id();

        Builder from(Subject from);

        @Nullable Subject from();

        Builder to(Room to);

        Builder createdAt(Instant createdAt);

        @Nullable Instant createdAt();

        Builder modifiedAt(Instant modifiedAt);

        @Nullable Instant modifiedAt();

        Builder content(String content);

        Builder format(Format format);

        Message build();
    }
}
