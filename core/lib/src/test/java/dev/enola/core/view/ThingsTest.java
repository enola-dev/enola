/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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
package dev.enola.core.view;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;

import dev.enola.core.proto.Thing;
import dev.enola.core.test.TestComplex;
import dev.enola.core.test.TestRepeated;
import dev.enola.core.test.TestSimple;

import org.junit.Test;

public class ThingsTest {

    TestSimple.Builder simple = TestSimple.newBuilder().setText("hello").setNumber(123);
    Thing.Struct.Builder simpleThingView =
            Thing.Struct.newBuilder()
                    .putFields("text", string("hello"))
                    .putFields("number", string("123"));

    @Test
    public void testSimple() {
        check(simple, simpleThingView);
    }

    @Test
    public void testTimestamp() {
        check(
                Timestamp.newBuilder().setSeconds(123).setNanos(456),
                "1970-01-01T00:02:03.000000456Z");
    }

    @Test
    public void testRepeated() {
        check(
                TestRepeated.newBuilder().addLines("one").addLines("two"),
                Thing.Struct.newBuilder().putFields("lines", list(string("one"), string("two"))));
    }

    @Test
    public void testComplex() {
        check(
                TestComplex.newBuilder().setSimple(simple).addSimples(simple).addSimples(simple),
                Thing.Struct.newBuilder()
                        .putFields("simple", struct(simpleThingView))
                        .putFields(
                                "simples", list(struct(simpleThingView), struct(simpleThingView))));
    }

    private void check(Message.Builder thing, String expectedText) {
        var view = Things.from(thing.build()).build();
        assertThat(view.hasStruct()).isFalse();
        assertThat(view.hasString()).isTrue();
        assertThat(view.getString()).isEqualTo(expectedText);
    }

    private void check(Message.Builder thing, Thing.Struct.Builder expectedView) {
        var view = Things.from(thing.build()).build();
        assertThat(view.hasStruct()).isTrue();
        assertThat(view.getStruct())
                .ignoringFields(Thing.Struct.TYPE_URI_FIELD_NUMBER)
                .isEqualTo(expectedView.build());
        if (view.hasStruct()) {
            assertThat(view.getStruct().getTypeUri()).isNotEmpty();
        }
    }

    private Thing string(String string) {
        return Thing.newBuilder().setString(string).build();
    }

    private Thing list(Thing... elements) {
        var valueList = Thing.List.newBuilder();
        for (var value : elements) {
            valueList.addEntries(value);
        }
        return Thing.newBuilder().setList(valueList).build();
    }

    private Thing struct(Thing.Struct.Builder thingView) {
        return Thing.newBuilder().setStruct(thingView).build();
    }
}
