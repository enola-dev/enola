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
package dev.enola.common.locale;

import dev.enola.common.context.Singleton;
import dev.enola.common.context.TLC;

import java.util.Locale;

/**
 * LocaleSupplierTLC is a {@link LocaleSupplier} implementation that looks up the current {@link
 * Locale} from the {@link TLC}. If it's not found there, then it checks the (static) {@link
 * #SINGLETON}. Otherwise, it falls back to one passed to the constructor.
 */
public final class LocaleSupplierTLC implements LocaleSupplier {

    public static final Singleton<Locale> SINGLETON = new Singleton<>() {};

    /**
     * Falls back to (variable) {@link Locale#getDefault()} if no Locale has been pushed to the
     * {@link TLC} nor set on the {@link #SINGLETON}.
     *
     * <p>Using this in tests could lead to flaky tests which depend on the local OS default.
     */
    public static final LocaleSupplier JVM_DEFAULT = new LocaleSupplierTLC(Locale.getDefault());

    /**
     * Falls back to (constant) {@link Locale#ROOT} if no Locale has been pushed to the {@link TLC}
     * nor set on the {@link #SINGLETON}.
     */
    public static final LocaleSupplier ROOT = new LocaleSupplierTLC(Locale.ROOT);

    private final Locale defaultLocale;

    /** Creates a new instance which falls back to the locale passed to this constructor. */
    public LocaleSupplierTLC(Locale fallbackLocale) {
        this.defaultLocale = fallbackLocale;
    }

    @Override
    public Locale get() {
        return TLC.optional(Locale.class).or(SINGLETON::getOptional).orElse(defaultLocale);
    }
}
