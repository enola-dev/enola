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
package dev.enola.model.enola.meta;

import dev.enola.model.enola.HasDescription;
import dev.enola.model.enola.HasIcon;
import dev.enola.model.enola.HasName;
import dev.enola.model.w3.rdfs.HasLabel;
import dev.enola.model.w3.rdfs.Resource;

public interface Common extends Resource, HasName, HasLabel, HasDescription, HasIcon {

    // skipcq: JAVA-E0169
    interface Builder<B extends Common>
            extends Common,
                    Resource.Builder<B>,
                    HasName.Builder<B>,
                    HasLabel.Builder<B>,
                    HasDescription.Builder<B>,
                    HasIcon.Builder<B> {}
}
