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
package dev.enola;

import dev.enola.thing.Thing;

import java.net.URI;

/**
 * 🕵🏾‍♀️ Enola ✨ <i>actions</i> ⬛ <i>Objects</i>, such as 📄 {@link
 * dev.enola.common.io.resource.Resource}s and structured 💾 data in 🔗 linked 👽 {@link Thing}s.
 *
 * <p>This helps to bring structure to models. 🔮 AGI singularity is around the corner.
 *
 * <p>"Resistance is futile" (said the Borg).
 */
public interface Enola {

    // TODO Invert? action-object instead object-action is more familiar...

    Object act(String objectIRI, String actionIRI);

    Object act(URI object, Thing actionThing);

    Object act(String objectIRI, Thing actionThing);
}
