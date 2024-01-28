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

import dev.enola.core.proto.List;
import dev.enola.core.proto.ThingView;
import dev.enola.core.proto.Value;
import dev.enola.core.test.TestComplex;
import dev.enola.core.test.TestRepeated;
import dev.enola.core.test.TestSimple;

import org.junit.Test;

public class ThingsViewsTest {

    TestSimple.Builder simple = TestSimple.newBuilder().setText("hello").setNumber(123);
    ThingView.Builder simpleThingView =
            ThingView.newBuilder()
                    .putFields("text", string("hello"))
                    .putFields("number", string("123"));

    @Test
    public void testSimple() {
        check(simple, simpleThingView);
    }

    @Test
    public void testTimestamp() {
        // TODO Later this will probably be transformed more specially...
        check(
                Timestamp.newBuilder().setSeconds(123).setNanos(456),
                ThingView.newBuilder()
                        .putFields("seconds", string("123"))
                        .putFields("nanos", string("456")));
    }

    @Test
    public void testRepeated() {
        check(
                TestRepeated.newBuilder().addLines("one").addLines("two"),
                ThingView.newBuilder().putFields("lines", list(string("one"), string("two"))));
    }

    @Test
    public void testComplex() {
        check(
                TestComplex.newBuilder().setSimple(simple).addSimples(simple).addSimples(simple),
                ThingView.newBuilder()
                        .putFields("simple", struct(simpleThingView))
                        .putFields(
                                "simples", list(struct(simpleThingView), struct(simpleThingView))));
    }

    private void check(Message.Builder thing, ThingView.Builder expectedView) {
        var view = ThingViews.from(thing.build()).build();
        assertThat(view)
                .ignoringFields(ThingView.TYPE_URI_FIELD_NUMBER)
                .isEqualTo(expectedView.build());
        assertThat(view.getTypeUri()).isNotEmpty();
    }

    private Value string(String string) {
        return Value.newBuilder().setString(string).build();
    }

    private Value list(Value... elements) {
        var valueList = List.newBuilder();
        for (Value value : elements) {
            valueList.addEntries(value);
        }
        return Value.newBuilder().setList(valueList).build();
    }

    private Value struct(ThingView.Builder thingView) {
        return Value.newBuilder().setStruct(thingView).build();
    }
}
