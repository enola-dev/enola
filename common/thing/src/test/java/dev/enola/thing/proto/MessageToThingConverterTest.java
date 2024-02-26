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
import dev.enola.core.test.TestComplex;
import dev.enola.core.test.TestRepeated;
import dev.enola.core.test.TestSimple;
import dev.enola.thing.Thing;
import dev.enola.thing.Value;

import org.junit.Test;

public class MessageToThingConverterTest {

    MessageToThingConverter c = new MessageToThingConverter();

    TestSimple.Builder simple = TestSimple.newBuilder().setText("hello").setNumber(123);
    Thing.Builder simpleThing =
            Thing.newBuilder()
                    .putFields("text", c.toValue("hello").build())
                    .putFields("number", c.toValue("123").build());

    Thing.Builder simpleThingWithProto =
            ProtoTypes.addProtoField(Thing.newBuilder(simpleThing.build()), simple);

    @Test
    public void testSimple() throws ConversionException {
        check(simple, simpleThingWithProto);
    }

    /* TODO Activate testTimestamp()
    @Test
    public void testTimestamp() throws ConversionException {
        check(
                Timestamp.newBuilder().setSeconds(123).setNanos(456),
                "1970-01-01T00:02:03.000000456Z");
    } */

    @Test
    public void testRepeated() throws ConversionException {
        var repeated = TestRepeated.newBuilder().addLines("one").addLines("two");
        var repeatedThing =
                Thing.newBuilder()
                        .putFields("lines", c.toList(c.toValue("one"), c.toValue("two")).build());
        var repeatedThingWithProto = ProtoTypes.addProtoField(repeatedThing, repeated);
        check(repeated, repeatedThingWithProto);
    }

    @Test
    public void testComplex() throws ConversionException {
        var complex =
                TestComplex.newBuilder().setSimple(simple).addSimples(simple).addSimples(simple);
        var complexThing =
                Thing.newBuilder()
                        .putFields("simple", struct(simpleThing).build())
                        .putFields(
                                "simples",
                                c.toList(struct(simpleThing), struct(simpleThing)).build());
        var complexThingWithProto = ProtoTypes.addProtoField(complexThing, complex);
        check(complex, complexThingWithProto);
    }

    /*
    private void check(Message.Builder thing, String expectedText) throws ConversionException {
        var view = Things.from(thing.build()).build();
        assertThat(view.hasStruct()).isFalse();
        assertThat(view.hasText()).isTrue();
        assertThat(view.getText().getString()).isEqualTo(expectedText);
    }
    */

    private void check(Message.Builder message, Thing.Builder expectedThing)
            throws ConversionException {
        var actualThing = c.convert(message.build());
        ProtoTruth.assertThat(actualThing.build()).isEqualTo(expectedThing.build());
    }

    private Value.Builder struct(Thing.Builder thing) {
        return Value.newBuilder().setStruct(thing);
    }
}
