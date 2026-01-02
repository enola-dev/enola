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
package dev.enola.common.convert;

import java.io.IOException;
import java.util.Optional;

public class IdentityObjectClassConverter implements ObjectClassConverter<Object> {

    @Override
    public <T> Optional<T> convertToType(Object input, Class<T> type) throws IOException {
        if (input != null && type.isAssignableFrom(input.getClass())) return Optional.of((T) input);
        return Optional.empty();
    }
}
