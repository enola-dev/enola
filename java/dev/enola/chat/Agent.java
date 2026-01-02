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

import java.util.function.Consumer;

/**
 * Agent is something which acts on a {@link Message}.
 *
 * <p>Agents can use other agents, web APIs, etc. to act. Agents interact with an environment.
 *
 * <p>Agents are also known as a "Bots" or "Tool/s (Providers)" or "Functions" or "Services" or
 * "Service Providers".
 */
public interface Agent extends Consumer<Message> {

    Subject subject();
}
