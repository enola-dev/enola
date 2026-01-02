/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

import static dev.enola.common.io.resource.SPI.missingCharsetExceptionSupplier;

import com.google.common.io.ByteSink;
import com.google.common.io.CharSink;

public interface WritableResource extends AbstractResource {

    ByteSink byteSink();

    // TODO Consider replacing or integrating this with Converter?!
    default CharSink charSink() {
        // Workaround for java.lang.NoSuchMethodError: 'java.util.Optional
        // com.google.common.base.Optional.toJavaUtil()' due to classpath hell :(
        var charset = java.util.Optional.ofNullable(mediaType().charset().orNull());
        return byteSink().asCharSink(charset.orElseThrow(missingCharsetExceptionSupplier(this)));
    }
}
