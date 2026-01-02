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
package dev.enola.data;

public class OptimisticLockingException extends RuntimeException {
    // TODO Avoid the (expensive) need for catching an Exception by "building it into Store"
    // TODO extends RuntimeException instead of Exception

    public OptimisticLockingException(String iri, long expected, long actual) {
        super(message(iri, expected, actual));
    }

    public OptimisticLockingException(String message, OptimisticLockingException cause) {
        super(message, cause);
    }

    private static String message(String iri, long expected, long actual) {
        return "OptimisticLockingException: "
                + iri
                + ", expected: "
                + expected
                + ", actual: "
                + actual;
    }
}
