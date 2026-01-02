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

import java.util.function.Consumer;

/** <a href="https://en.wikipedia.org/wiki/Business_telephone_system">PBX</a>. */
public interface Switchboard {

    /** Post a Message to the switchboard. */
    void post(Message.Builder message);

    /**
     * Watch the switchboard for new messages.
     *
     * <p>This is intended to be used mostly internally; "applications" should write {@link Agent}s,
     * instead.
     */
    // TODO Move this to a separate interface? Or not needed, because everything could be an agent?!
    void watch(Consumer<Message> consumer);
}
