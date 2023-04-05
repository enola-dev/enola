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
package dev.enola.common.io.resource;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.common.net.MediaType;
import java.io.IOException;
import org.junit.Test;

public class MemoryResourceTest {
  private static final byte[] BYTES = new byte[] {1, 2, 3};
  private static final String TEXT = "hello, world";

  @Test
  public void testBinaryMemoryResource() throws IOException {
    MemoryResource resource = new MemoryResource(MediaType.OCTET_STREAM);
    resource.byteSink().write(BYTES);
    assertThat(resource.byteSource().read()).isEqualTo(BYTES);

    assertThrows(IllegalStateException.class, () -> resource.charSink());
    assertThrows(IllegalStateException.class, () -> resource.charSource());
  }

  @Test
  public void testTextMemoryResource() throws IOException {
    MemoryResource resource = new MemoryResource(MediaType.PLAIN_TEXT_UTF_8);
    resource.charSink().write(TEXT);
    assertThat(resource.charSource().read()).isEqualTo(TEXT);
  }
}
