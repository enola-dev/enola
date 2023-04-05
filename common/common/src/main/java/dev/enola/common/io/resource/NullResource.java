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

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

class NullResource implements Resource {

  static final NullResource INSTANCE = new NullResource();

  static final String SCHEME = "null";

  private static final URI NULL_URI = URI.create("null:-");
  private static final MediaType MEDIA_TYPE = MediaType.OCTET_STREAM;

  private NullResource() {}

  @Override
  public URI uri() {
    return NULL_URI;
  }

  @Override
  public MediaType mediaType() {
    return MEDIA_TYPE;
  }

  @Override
  public ByteSink byteSink() {
    return NullByteSink.INSTANCE;
  }

  @Override
  public ByteSource byteSource() {
    return NullByteSource.INSTANCE;
  }

  // TODO https://github.com/google/guava/issues/2011
  private static final class NullByteSink extends ByteSink {
    public static final ByteSink INSTANCE = new NullByteSink();

    private NullByteSink() {}

    @Override
    public OutputStream openStream() throws IOException {
      return ByteStreams.nullOutputStream();
    }
  }

  private static final class NullByteSource extends ByteSource {
    public static final ByteSource INSTANCE = new NullByteSource();

    private NullByteSource() {}

    @Override
    public InputStream openStream() throws IOException {
      return new NullInputStream();
    }
  }

  private static final class NullInputStream extends InputStream {
    @Override
    public int read() throws IOException {
      return 0;
    }
  }
}
