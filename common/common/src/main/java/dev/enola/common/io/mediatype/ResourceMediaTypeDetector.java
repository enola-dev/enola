/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.mediatype;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.AbstractResource;
import dev.enola.common.io.resource.Resource;

import java.util.Optional;

/** Detects a (better) MediaType of a Resource. */
// TODO Clarify & document for who this is - Resource API users, or implementors?!
// See the related comments on MediaTypeProvider#detect() ...
public interface ResourceMediaTypeDetector {

    /**
     * Detect the MediaType of a {@link Resource}. This is (currently) based on its (supposed)
     * existing {@link AbstractResource#mediaType()} and (if required) e.g. an file name extension
     * from its {@link AbstractResource#uri()}. (It does not "sniff" the content, yet.)
     */
    Optional<MediaType> detect(AbstractResource resource);
    // TODO Replace Optional<MediaType> with just MediaType
    // and return .orElseGet(() -> resource.mediaType())
}
