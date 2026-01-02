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
package dev.enola.model.enola.mediatype;

import dev.enola.model.enola.HasChildren;
import dev.enola.model.enola.HasParent;
import dev.enola.model.w3.rdf.HasClass;
import dev.enola.model.w3.rdfs.HasComment;
import dev.enola.model.w3.rdfs.HasLabel;
import dev.enola.model.w3.rdfs.HasSeeAlso;

public interface MediaType
        extends HasClass,
                HasLabel,
                HasComment,
                HasSeeAlso,
                HasFileExtensions,
                HasMediaType,
                HasParent,
                HasChildren {

    // In theory: interface Builder<B extends MediaType> extends ...
    // In practice, we know we're not going to further extend MediaType, so just:
    interface Builder // skipcq: JAVA-E0169
    extends HasClass.Builder<MediaType>,
                    HasLabel.Builder<MediaType>,
                    HasComment.Builder<MediaType>,
                    HasSeeAlso.Builder<MediaType>,
                    HasFileExtensions.Builder<MediaType>,
                    HasMediaType.Builder<MediaType>,
                    HasParent.Builder<MediaType>,
                    HasChildren.Builder<MediaType> {}
}
