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
package dev.enola.common.protobuf;

import dev.enola.common.validation.Validations;

public class ValidationException extends Exception {
    private final Validations proto;

    public ValidationException(Validations proto) {
        super(createMessage(proto));
        this.proto = proto;
    }

    private static String createMessage(Validations proto) {
        var n = proto.getValidationsCount();
        var sb = new StringBuilder(n + " model validation error/s...\n");
        for (int i = 0; i < n; i++) {
            var v = proto.getValidations(i);
            sb.append("\t" + (i + 1) + ". " + v.getPath() + " : " + v.getError() + "\n");
        }
        return sb.toString();
    }

    public Validations getProto() {
        return proto;
    }
}
