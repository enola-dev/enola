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

import static com.google.common.base.Strings.emptyToNull;

import static java.util.Objects.requireNonNull;

import dev.enola.identity.Subject;

import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;

// TODO This is temporary, until replaced by Thing
//   or https://komma.enilink.net/docs/ ?
//   and https://immutables.github.io/ ?
public record MessageImpl(
        Object id,
        Optional<Object> replyTo,
        Subject from,
        Room to,
        Instant createdAt,
        Instant modifiedAt,
        Optional<String> subject,
        String content,
        Format format)
        implements Message {

    Builder builder() {
        return new Builder(
                id,
                replyTo.orElse(null),
                from,
                to,
                createdAt,
                modifiedAt,
                subject.orElse(null),
                content,
                format);
    }

    static class Builder implements Message.Builder { // skipcq: JAVA-E0169

        private @Nullable Object id;
        private @Nullable Object replyTo;
        private @Nullable Subject from;
        private @Nullable Room to;
        private @Nullable Instant created;
        private @Nullable Instant modified;
        private @Nullable String subject;
        private @Nullable String content;
        private Format format = Format.PLAIN;

        Builder(
                Object id,
                @Nullable Object replyTo,
                Subject from,
                Room to,
                Instant createdAt,
                Instant modifiedAt,
                @Nullable String subject,
                String content,
                Format format) {
            this.id = id;
            this.replyTo = replyTo;
            this.from = from;
            this.to = to;
            this.created = createdAt;
            this.modified = modifiedAt;
            this.subject = subject;
            this.content = content;
            this.format = format;
        }

        public Builder() {}

        @Override
        public Message.Builder id(Object id) {
            this.id = requireNonNull(id);
            return this;
        }

        @Override
        public Object id() {
            return id;
        }

        @Override
        public Message.Builder replyTo(Object replyToMessageID) {
            this.replyTo = requireNonNull(replyToMessageID);
            return this;
        }

        @Override
        public Message.Builder from(Subject from) {
            this.from = requireNonNull(from);
            return this;
        }

        @Override
        public @Nullable Subject from() {
            return from;
        }

        @Override
        public Message.Builder to(Room to) {
            this.to = requireNonNull(to);
            return this;
        }

        @Override
        public Message.Builder createdAt(Instant createdAt) {
            this.created = requireNonNull(createdAt);
            return this;
        }

        @Override
        public @Nullable Instant createdAt() {
            return created;
        }

        @Override
        public Message.Builder modifiedAt(Instant modifiedAt) {
            this.modified = requireNonNull(modifiedAt);
            return this;
        }

        @Override
        public @Nullable Instant modifiedAt() {
            return modified;
        }

        @Override
        public Message.Builder subject(String subject) {
            this.subject = requireNonNull(emptyToNull(subject.trim()));
            return this;
        }

        @Override
        public Message.Builder content(String content) {
            this.content = requireNonNull(emptyToNull(content.trim()));
            return this;
        }

        @Override
        public Message.Builder format(Format format) {
            this.format = requireNonNull(format);
            return this;
        }

        @Override
        public Message build() {
            return new MessageImpl(
                    requireNonNull(id, "id"),
                    Optional.ofNullable(replyTo),
                    requireNonNull(from, "from"),
                    requireNonNull(to, "to"),
                    requireNonNull(created, "created"),
                    requireNonNull(modified, "modified"),
                    Optional.ofNullable(subject),
                    requireNonNull(content, "content"),
                    requireNonNull(format, "format"));
        }
    }
}
