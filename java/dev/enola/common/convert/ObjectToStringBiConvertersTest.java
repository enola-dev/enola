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
package dev.enola.common.convert;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Optional;

public class ObjectToStringBiConvertersTest {

    @Test
    public void BOOLEAN_convertToType() throws IOException {
        ObjectClassConverter occ = ObjectToStringBiConverters.BOOLEAN;
        assertThat(occ.convertToType(Boolean.TRUE, String.class)).hasValue("true");
        assertThat(occ.convertToType(Boolean.FALSE, String.class)).hasValue("false");
        assertThat(occ.convertToType(Boolean.FALSE, URI.class)).isEmpty();
        assertThat(occ.convertToType(null, String.class)).isEmpty();
        assertThat(occ.convertToType(Integer.valueOf(123), URI.class)).isEmpty();
        assertThat(occ.convertToType(Integer.valueOf(123), String.class)).isEmpty();
    }

    @Test
    public void STRING_convertToType() throws IOException {
        ObjectClassConverter occ = ObjectToStringBiConverters.STRING;
        assertThat(occ.convertToType(Integer.valueOf(123), String.class)).isEmpty();
        assertThat(occ.convertToType("hello, world", URI.class)).isEmpty();
    }

    @Test
    public void BOOLEAN_convertTo() throws IOException {
        BiConverter<Boolean, String> bic = ObjectToStringBiConverters.BOOLEAN;
        assertThat(bic.convertTo(Boolean.TRUE)).isEqualTo("true");
        assertThat(bic.convertTo(Boolean.FALSE)).isEqualTo("false");
        assertThat(bic.convertTo(null)).isEqualTo(null);
    }

    @Test
    public void BOOLEAN_convertFrom() throws IOException {
        BiConverter<Boolean, String> bic = ObjectToStringBiConverters.BOOLEAN;
        assertThat(bic.convertFrom("true")).isEqualTo(Boolean.TRUE);
        assertThat(bic.convertFrom("false")).isEqualTo(Boolean.FALSE);
        assertThat(bic.convertFrom(null)).isEqualTo(null);
    }

    @Test
    public void FILE_TIME_convertTo_Instant() throws IOException {
        var instant = Instant.now();
        var fileTime = FileTime.from(instant);
        var actual = ObjectToStringBiConverters.FILE_TIME.convertToType(fileTime, Instant.class);
        assertThat(actual).isEqualTo(Optional.of(instant));
    }
}
