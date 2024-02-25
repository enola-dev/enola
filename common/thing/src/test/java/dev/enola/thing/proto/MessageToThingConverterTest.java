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
package dev.enola.thing.proto;

import com.google.common.truth.extensions.proto.ProtoTruth;
import com.google.protobuf.Message;

import dev.enola.common.convert.ConversionException;
import dev.enola.core.test.TestSimple;
import dev.enola.thing.Thing;

import org.junit.Test;

public class MessageToThingConverterTest {

    MessageToThingConverter c = new MessageToThingConverter();

    TestSimple.Builder simple = TestSimple.newBuilder().setText("hello").setNumber(123);
    Thing.Builder simpleThing =
            Thing.newBuilder()
                    .putFields("text", c.toValue("hello").build())
                    .putFields("number", c.toValue("123").build());

    Thing.Builder simpleThingWithProto = ProtoTypes.addProtoField(simpleThing, simple);

    @Test
    public void testSimple() throws ConversionException {
        check(simple, simpleThingWithProto);
    }

    /* TODO Activate more old tests...

    @Test
    public void testTimestamp() throws ConversionException {
        check(
                Timestamp.newBuilder().setSeconds(123).setNanos(456),
                "1970-01-01T00:02:03.000000456Z");
    }

    @Test
    public void testRepeated() throws ConversionException {
        check(
                TestRepeated.newBuilder().addLines("one").addLines("two"),
                Thing.Struct.newBuilder()
                        .putFields("lines", list(string("one"), string("two")))
                        .putFields(
                                "$proto",
                                string(
                                        "dev.enola.core.test.TestRepeated",
                                        "enola:proto/dev.enola.core.test.TestRepeated"))
                        .build());
    }

    @Test
    public void testComplex() throws ConversionException {
        check(
                TestComplex.newBuilder().setSimple(simple).addSimples(simple).addSimples(simple),
                Thing.Struct.newBuilder()
                        .putFields("simple", struct(simpleThingView))
                        .putFields(
                                "simples", list(struct(simpleThingView), struct(simpleThingView)))
                        .putFields(
                                "$proto",
                                string(
                                        "dev.enola.core.test.TestComplex",
                                        "enola:proto/dev.enola.core.test.TestComplex"))
                        .build());
    }

    private void check(Message.Builder thing, String expectedText) throws ConversionException {
        var view = Things.from(thing.build()).build();
        assertThat(view.hasStruct()).isFalse();
        assertThat(view.hasText()).isTrue();
        assertThat(view.getText().getString()).isEqualTo(expectedText);
    }

    private Thing list(Thing... elements) throws ConversionException {
        var valueList = Thing.List.newBuilder();
        for (var value : elements) {
            valueList.addEntries(value);
        }
        return Thing.newBuilder().setList(valueList).build();
    }

    private Thing struct(Thing.Struct thingView) throws ConversionException {
        return Thing.newBuilder().setStruct(thingView).build();
    }
     */

    private void check(Message.Builder message, Thing.Builder expectedThing)
            throws ConversionException {
        var actualThing = c.convert(message.build());
        ProtoTruth.assertThat(actualThing.build()).isEqualTo(expectedThing.build());
    }
}
