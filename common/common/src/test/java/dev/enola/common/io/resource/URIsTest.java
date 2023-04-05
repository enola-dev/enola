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

import com.google.common.io.Resources;
import com.google.common.truth.Truth;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;

public class URIsTest {
  @Test
  public void testGetFilename() throws URISyntaxException {
    // Files
    assertThat(URI.create(""), "");
    assertThat(new File("test.txt").toURI(), "test.txt");
    assertThat(new File("/test.txt").toURI(), "test.txt");
    assertThat(new File("/home/test.txt").toURI(), "test.txt");
    assertThat(new File("/").toURI(), "");
    assertThat(new File("directory").toURI(), "directory");
    assertThat(new File("/directory").toURI(), "directory");
    assertThat(new File("/directory/").toURI(), "directory");
    assertThat(new File(".").toURI(), "");
    assertThat(new File(".").getAbsoluteFile().toURI(), "");
    assertThat(new File("./").toURI(), "");
    assertThat(new File("./").getAbsoluteFile().toURI(), "");

    // Windows Files
    // TODO assertThat(new File("C:\\WINDOWS\\logo.bmp").toURI(), "logo.bmp");

    // HTTP
    assertThat(URI.create("http://www.vorburger.ch"), "");
    assertThat(URI.create("https://www.vorburger.ch"), "");
    assertThat(URI.create("https://www.vorburger.ch/"), "");
    assertThat(URI.create("https://www.vorburger.ch/index.html"), "index.html");
    assertThat(URI.create("https://www.vorburger.ch/index.html#toot"), "index.html");
    assertThat(URI.create("https://www.vorburger.ch/index.html?search=1998"), "index.html");
    assertThat(URI.create("https://www.vorburger.ch/projects/"), "");
    assertThat(URI.create("https://www.vorburger.ch/projects/coc/coc_src.html"), "coc_src.html");
    assertThat(URI.create("https://www.vorburger.ch/space%20file"), "space file");

    // Classpath Resources
    assertThat(Resources.getResource("empty").toURI(), "empty");
    assertThat(
        Resources.getResource("META-INF/services/dev.enola.common.io.mediatype.MediaTypeProvider")
            .toURI(),
        "dev.enola.common.io.mediatype.MediaTypeProvider");

    // No schema - this is correct!
    assertThat(URI.create("test.txt"), "");
    assertThat(URI.create(""), "");

    Assert.assertThrows(NullPointerException.class, () -> URI.create(null));
  }

  private void assertThat(URI uri, String expectedFilename) {
    Truth.assertThat(URIs.getFilename(uri)).isEqualTo(expectedFilename);
  }
}
