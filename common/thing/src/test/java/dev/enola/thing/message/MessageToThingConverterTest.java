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
package dev.enola.thing.message;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.truth.extensions.proto.ProtoTruth;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.protobuf.Timestamps2;
import dev.enola.core.test.TestComplex;
import dev.enola.core.test.TestEnum;
import dev.enola.core.test.TestRepeated;
import dev.enola.core.test.TestSimple;
import dev.enola.thing.KIRI;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Thing.Builder;
import dev.enola.thing.proto.Value;

import org.junit.Test;

import java.time.Instant;

public class MessageToThingConverterTest {

    private static final String TEST_THING_IRI = "http://test/thing";

    MessageToThingConverter c = new MessageToThingConverter();

    Timestamp ts = Timestamps2.fromInstant(Instant.now());

    ByteString bytes = ByteString.copyFrom(new byte[] {1, 2, 3});

    TestSimple.Builder simple =
            TestSimple.newBuilder()
                    .setText("hello")
                    .setNumber(UnsignedInteger.MAX_VALUE.intValue())
                    .setTs(ts)
                    .setBytes(bytes)
                    .setAnenum(TestEnum.TEST_ENUM_B);

    Thing.Builder simpleThing =
            Thing.newBuilder()
                    .putFields(
                            getFieldIRI(TestSimple.getDescriptor(), TestSimple.TEXT_FIELD_NUMBER),
                            c.toValue("hello").build())
                    .putFields(
                            getFieldIRI(TestSimple.getDescriptor(), TestSimple.NUMBER_FIELD_NUMBER),
                            c.toLiteral("4294967295", KIRI.XSD.UINT32).build())
                    .putFields(
                            getFieldIRI(TestSimple.getDescriptor(), TestSimple.TS_FIELD_NUMBER),
                            c.toLiteral(Timestamps.toString(ts), KIRI.XSD.TS).build())
                    .putFields(
                            getFieldIRI(TestSimple.getDescriptor(), TestSimple.BYTES_FIELD_NUMBER),
                            c.toLiteral("mAQID", KIRI.XSD.BIN64).build())
                    .putFields(
                            getFieldIRI(TestSimple.getDescriptor(), TestSimple.ANENUM_FIELD_NUMBER),
                            c.toLink(
                                            ProtoTypes.getEnumValueERI(
                                                    TestEnum.TEST_ENUM_B.getValueDescriptor()),
                                            "TEST_ENUM_B")
                                    .build());

    Thing.Builder simpleThingWithProto = headers(Thing.newBuilder(simpleThing.build()), simple);

    private String getFieldIRI(Descriptor descriptor, int i) {
        return ProtoTypes.getFieldERI(descriptor.findFieldByNumber(i));
    }

    private Builder headers(Thing.Builder thing, MessageOrBuilder message) {
        thing.setIri(TEST_THING_IRI);
        return ProtoTypes.addProtoField(thing, message);
    }

    @Test
    public void testSimple() throws ConversionException {
        check(simple, simpleThingWithProto);
    }

    @Test
    public void testRepeated() throws ConversionException {
        var repeated = TestRepeated.newBuilder().addLines("one").addLines("two");
        var repeatedThing =
                Thing.newBuilder()
                        .putFields(
                                "enola:/enola.dev/proto/field/dev.enola.thing.test.TestRepeated/1",
                                c.toList(c.toValue("one"), c.toValue("two")).build());
        var repeatedThingWithProto = headers(repeatedThing, repeated);
        check(repeated, repeatedThingWithProto);
    }

    @Test
    public void testComplex() throws ConversionException {
        var complex =
                TestComplex.newBuilder().setSimple(simple).addSimples(simple).addSimples(simple);
        var complexThing =
                Thing.newBuilder()
                        .putFields(
                                getFieldIRI(
                                        TestComplex.getDescriptor(),
                                        TestComplex.SIMPLE_FIELD_NUMBER),
                                struct(simpleThing).build())
                        .putFields(
                                getFieldIRI(
                                        TestComplex.getDescriptor(),
                                        TestComplex.SIMPLES_FIELD_NUMBER),
                                c.toList(struct(simpleThing), struct(simpleThing)).build());
        var complexThingWithProto = headers(complexThing, complex);
        check(complex, complexThingWithProto);
    }

    private void check(Message.Builder message, Thing.Builder expectedThing)
            throws ConversionException {
        var actualThing = c.convert(new MessageWithIRI(TEST_THING_IRI, message.build()));
        ProtoTruth.assertThat(actualThing.build()).isEqualTo(expectedThing.build());
    }

    private Value.Builder struct(Thing.Builder thing) {
        return Value.newBuilder().setStruct(thing);
    }
}
