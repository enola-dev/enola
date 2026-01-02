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
package dev.enola.common.xml;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.Converter;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class TemporalToXmlStringConverter implements Converter<TemporalAccessor, String> {

    /*
        private final DatatypeFactory datatypeFactory;

        public TemporalToXmlStringConverter() {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                throw new IllegalStateException(e);
            }
        }
    */

    private final boolean strictFourDigitYears;

    /**
     * Constructor.
     *
     * @param strictFourDigitYears see <a
     *     href="https://en.wikipedia.org/wiki/ISO_8601#Years">Wikipedia on ISO 8601 years</a>, but
     *     note that <a href="https://www.w3.org/TR/xmlschema-2/#morethan9999years">XSD permits year
     *     values greater than 9999</a>.
     */
    public TemporalToXmlStringConverter(boolean strictFourDigitYears) {
        this.strictFourDigitYears = strictFourDigitYears;
    }

    @Override
    public String convert(TemporalAccessor input) throws ConversionException {
        // No need to go through a XMLGregorianCalendar!
        if (!strictFourDigitYears) return DateTimeFormatter.ISO_INSTANT.format(input);
        throw new IllegalArgumentException("TODO Missing implementation for: " + input.getClass());
    }
}
