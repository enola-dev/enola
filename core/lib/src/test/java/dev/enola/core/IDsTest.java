/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.core;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import dev.enola.core.proto.ID;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;

public class IDsTest {

    String path0 = "foo";
    URI uri0 = URI.create("enola:" + path0);
    ID proto0 = ID.newBuilder().setEntity("foo").build();

    String path1 = "demo.foo";
    URI uri1 = URI.create("enola:" + path1);
    ID proto1 = ID.newBuilder().setNs("demo").setEntity("foo").build();

    String path2 = "demo.foo/abc";
    URI uri2 = URI.create("enola:" + path2);
    ID proto2 = ID.newBuilder().setNs("demo").setEntity("foo").addPaths("abc").build();

    // String uri2 = "k8s:pod?network=prod&context=demo&namespace=test&name=hello";
    String path3 = "k8s.pod/prod/demo/test/hello";
    URI uri3 = URI.create("enola:" + path3);
    ID proto3 =
            ID.newBuilder()
                    .setNs("k8s")
                    .setEntity("pod")
                    .addPaths("prod") // "network"
                    .addPaths("demo") // "context"
                    .addPaths("test") // "namespace"
                    .addPaths("hello") // "name"
                    .build();

    @Test
    public void testToPath() {
        assertThat(IDs.toPath(proto0)).isEqualTo(path0);
        assertThat(IDs.toPath(proto1)).isEqualTo(path1);
        assertThat(IDs.toPath(proto2)).isEqualTo(path2);
        assertThat(IDs.toPath(proto3)).isEqualTo(path3);
    }

    @Test
    public void testToURI() {
        assertThat(IDs.toPathURI(proto0)).isEqualTo(uri0);
        assertThat(IDs.toPathURI(proto1)).isEqualTo(uri1);
        assertThat(IDs.toPathURI(proto2)).isEqualTo(uri2);
        assertThat(IDs.toPathURI(proto3)).isEqualTo(uri3);
    }

    @Test
    public void testParse() {
        assertThat(IDs.parse(path0)).isEqualTo(proto0);
        assertThat(IDs.parse(path1)).isEqualTo(proto1);
        assertThat(IDs.parse(path2)).isEqualTo(proto2);
        assertThat(IDs.parse(path3)).isEqualTo(proto3);

        assertThat(IDs.parse(uri3.toString())).isEqualTo(proto3);
        // assertThat(IDs.parse(uri3.toString() + "?x=y#a")).isEqualTo(proto3);
    }

    @Test
    @Ignore // TODO FIXME
    public void testParseBad() {
        // Bad
        badEnolaID("|"); // Illegal Character
        badEnolaID("demo:fo|o"); // Still illegal Character
        badEnolaID("demo:"); // Still Path required
        badEnolaID("demo://foo"); // No "authority"
        badEnolaID("demo://foo/path"); // Still no "authority"
        badEnolaID("demo:foo#fragment"); // No Fragments (?)
        badEnolaID("demo:foo?bad=a=b"); // Nope, this is confusing
        badEnolaID("demo:foo?bad=a&bad=b"); // Duplicate query names
    }

    private void badEnolaID(String id) {
        assertThrows(IllegalArgumentException.class, () -> IDs.parse(id));
    }
}
