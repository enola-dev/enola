/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.object.csv;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.net.MediaType;

import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.common.io.resource.StringResource2;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

public class CsvMapReaderTest {

    ObjectReader csvReader = new CsvReader();

    @Test
    public void empty() throws IOException {
        var result = csvReader.readStream(new EmptyResource(MediaType.CSV_UTF_8), Map.class);
        assertThat(result).isEmpty();
    }

    @Test
    public void csv1() throws IOException {
        var csv = "name,age\nAlice,30\nBob,25\n";
        var resource =
                StringResource2.of(csv, MediaType.CSV_UTF_8, URI.create("string://test.csv"));
        var result = csvReader.readStream(resource, Map.class);
        assertThat(result)
                .containsExactly(
                        Map.of("name", "Alice", "age", "30"), Map.of("name", "Bob", "age", "25"))
                .inOrder();
    }

    @Test
    public void learningCsv() throws IOException {
        var csv = "name,age\nAlice,30\nBob,25\n";
        var reader = new StringReader(csv);
        var list = new ArrayList<Map<String, String>>();
        try (CSVParser csvParser =
                CSVFormat.RFC4180
                        .builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .get()
                        .parse(reader)) {
            for (CSVRecord record : csvParser) {
                list.add(record.toMap());
                // throw new IOException(record.toString());
            }
        }
        assertThat(list)
                .containsExactly(
                        Map.of("name", "Alice", "age", "30"), Map.of("name", "Bob", "age", "25"))
                .inOrder();
    }
}
