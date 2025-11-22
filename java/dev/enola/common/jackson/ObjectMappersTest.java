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
package dev.enola.common.jackson;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import org.junit.Test;

public class ObjectMappersTest {

    static record Something(String name) {}

    String expectedJSON = "{\"name\":\"test\"}";
    String jsonWithUnknown = "{\"name\":\"test\",\"unknown\":\"property\"}";

    @Test
    public void testSomething() throws Exception {
        Something something = new Something("test");
        ObjectMapper objectMapper = ObjectMappers.INSTANCE;
        String json = objectMapper.writeValueAsString(something);
        assertThat(json).isEqualTo(expectedJSON);

        Something something2 = objectMapper.readValue(json, Something.class);
        assertThat(something).isEqualTo(something2);
    }

    @Test(expected = UnrecognizedPropertyException.class)
    public void testSomethingWithUnknown() throws Exception {
        ObjectMappers.INSTANCE.readValue(jsonWithUnknown, Something.class);
    }

    @Test
    public void newObjectMapperIsIndependent() throws Exception {
        ObjectMapper newMapper = ObjectMappers.newObjectMapper();
        newMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        Something deserializedWithNewMapper = newMapper.readValue(jsonWithUnknown, Something.class);
        assertThat(deserializedWithNewMapper.name()).isEqualTo("test");
    }
}
