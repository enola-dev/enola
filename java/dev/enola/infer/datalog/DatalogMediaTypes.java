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
package dev.enola.infer.datalog;

import static com.google.common.net.MediaType.create;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;

import java.nio.charset.StandardCharsets;

/**
 * The <code>text/vnd.datalog</code> Media Types for <a
 * href="Datalog">https://en.wikipedia.org/wiki/Datalog</a>; as specified by the <a
 * href="https://www.iana.org/assignments/media-types/application/vnd.datalog">iana.org
 * assignment</a>.
 *
 * <p>Nota bene, quote, Wikipedia: <i>"There is no unified standard for the specification of Datalog
 * syntax."</i>
 */
public class DatalogMediaTypes implements MediaTypeProvider {

    public static final MediaType GENERIC_DATALOG_UTF_8 =
            create("text", "vnd.datalog").withCharset(StandardCharsets.UTF_8);

    public static final String FEATURES = "features";

    public static final String DIALECT = "dialect";

    /** <a href="https://souffle-lang.github.io">Soufflé</a> dialect. */
    public static final MediaType SOUFFLE_DATALOG_UTF_8 =
            GENERIC_DATALOG_UTF_8.withParameter(DIALECT, "souffle");

    @Override
    public Multimap<String, MediaType> extensionsToTypes() {
        return ImmutableMultimap.of(
                ".souffle.dl",
                SOUFFLE_DATALOG_UTF_8,
                ".dl",
                GENERIC_DATALOG_UTF_8,
                ".dtlg",
                GENERIC_DATALOG_UTF_8);
    }

    // TODO https://enola.dev/mediaType/text/vnd-datalog?dialect=souffle&charset=UTF-8
}
