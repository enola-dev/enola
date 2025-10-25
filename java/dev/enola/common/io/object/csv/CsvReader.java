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

import static dev.enola.common.collect.MoreIterators.map;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import dev.enola.common.collect.MoreIterators;
import dev.enola.common.function.CloseableIterable;
import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.resource.ReadableResource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class CsvReader implements ObjectReader {

    private final CSVFormat csvFormat;

    public CsvReader(CSVFormat csvFormat) {
        this.csvFormat = csvFormat;
    }

    public CsvReader() {
        this(CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).get());
    }

    @Override
    public <T> Optional<T> optional(ReadableResource resource, Class<T> type) throws IOException {
        throw new UnsupportedOperationException("Use readStream or readArray for CSV reading");
    }

    @Override
    @Deprecated
    public <T> Iterable<T> readArray(ReadableResource resource, Class<T> type) throws IOException {
        return readStream(resource, type);
    }

    @Override
    public <T> CloseableIterable<T> readStream(ReadableResource resource, Class<T> type)
            throws IOException {
        if (!type.isAssignableFrom(Map.class))
            throw new IllegalArgumentException("CsvReader currently only supports Map types");

        if (!MediaTypes.normalizedNoParamsEquals(resource.mediaType(), MediaType.CSV_UTF_8))
            return CloseableIterable.empty();

        Reader reader = resource.charSource().openStream();
        CSVParser csvParser = csvFormat.parse(reader);
        Iterator<CSVRecord> csvIterator = csvParser.iterator();
        Iterator<Map<String, String>> mapIterator = map(csvIterator, CsvReader::csvRecordToMap);
        Iterable<Map<String, String>> mapIterable = MoreIterators.toIterable(mapIterator);
        CloseableIterable<Map<String, String>> closeableMapIterable =
                CloseableIterable.wrap(
                        mapIterable,
                        () -> {
                            csvParser.close();
                            reader.close();
                        });

        @SuppressWarnings("unchecked")
        CloseableIterable<T> mapsT = (CloseableIterable<T>) closeableMapIterable;
        return mapsT;
    }

    @VisibleForTesting
    static Map<String, String> csvRecordToMap(CSVRecord csvRecord) {
        var mapBuilder = ImmutableMap.<String, String>builderWithExpectedSize(csvRecord.size());
        csvRecord.toMap().forEach(mapBuilder::put);
        return mapBuilder.build();
    }
}
