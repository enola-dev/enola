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
package dev.enola.common.io.resource;

import com.google.common.net.MediaType;

import java.net.URI;

public interface AbstractResource {

    /** {@link URI} where the (bytes) content of resource can be read from or written to. */
    URI uri();

    /**
     * {@link MediaType} describing the format of this resource. This is always present, but could
     * be wrong; a {@link dev.enola.common.io.mediatype.ResourceMediaTypeDetector} may find a better
     * one. TODO This doc contradicts ResourceMediaTypeDetector's... so which one is it?! ;-)
     */
    MediaType mediaType();
}
