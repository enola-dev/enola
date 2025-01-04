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
package dev.enola.common.io.hashbrown;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.*;

import org.junit.Rule;
import org.junit.Test;

public class IntegrityValidatingDelegatingResourceTest {

    ResourceProvider rp =
            new IntegrityValidatingDelegatingResource.Provider(new ClasspathResource.Provider());

    public @Rule SingletonRule r1 = $(MediaTypeProviders.set());

    @Test(expected = IntegrityViolationException.class)
    public void bad() {
        rp.get("classpath:/test.png?integrity=m1QEQAAAAAAAAAAAAAAAAAAAAAA").charSource();
        // TODO Validate that IntegrityViolationException contains m1... and not fz...

        // TODO Remove...
        /*
        throw new RuntimeException(
                Multihashes.toString(
                        new Multihash(
                                Multihash.Type.md5,
                                new byte[] {
                                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                }),
                        Multibase.Base.Base64));
         */
    }

    @Test
    public void good() {
        rp.get("classpath:/test.png?integrity=m1QEQtoy0Os8CMvMKItSdcFkRow").byteSource();
    }
}
