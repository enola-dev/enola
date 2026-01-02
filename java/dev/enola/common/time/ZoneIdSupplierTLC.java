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
package dev.enola.common.time;

import dev.enola.common.context.Singleton;
import dev.enola.common.context.TLC;

import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * ZoneIdSupplierTLC is a {@link ZoneIdSupplier} implementation that looks up the current TZ from
 * the {@link TLC}. If it's not found there, then it checks the (static) {@link #SINGLETON}.
 * Otherwise, it falls back to one passed to the constructor.
 */
public final class ZoneIdSupplierTLC implements ZoneIdSupplier {

    public static final Singleton<ZoneId> SINGLETON = new Singleton<>() {};

    /**
     * Falls back to (variable) {@link ZoneId#systemDefault()} if no TZ has been pushed to the
     * {@link TLC} nor set on the {@link #SINGLETON}.
     *
     * <p>Using this in tests could lead to flaky tests which depend on the local OS default.
     */
    public static final ZoneIdSupplierTLC JVM_DEFAULT =
            new ZoneIdSupplierTLC(ZoneId.systemDefault());

    /**
     * Falls back to (constant) {@link ZoneOffset#UTC} if no Locale has been pushed to the {@link
     * TLC} nor set on the {@link #SINGLETON}.
     */
    public static final ZoneIdSupplierTLC UTC = new ZoneIdSupplierTLC(ZoneOffset.UTC);

    private final ZoneId falbackZoneId;

    /** Creates a new instance which falls back to the TZ passed to this constructor. */
    public ZoneIdSupplierTLC(ZoneId fallback) {
        this.falbackZoneId = fallback;
    }

    @Override
    public ZoneId get() {
        return TLC.optional(ZoneId.class).or(SINGLETON::getOptional).orElse(falbackZoneId);
    }
}
