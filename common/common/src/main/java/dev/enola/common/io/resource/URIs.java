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

import com.google.common.base.Strings;
import java.net.URI;

/**
 * See also {@link com.google.common.io.Files#getFileExtension(String)} and @{@link
 * com.google.common.io.Files#getNameWithoutExtension(String)}.
 */
public final class URIs {

  /**
   * Extracts the "file name" from an URI, or the empty string if there is none. The filename is
   * simply the last part of the path of the URI. It COULD be a directory! Works for file: http: and
   * other even for "weird" URIs, such as those from Classpath URLs.
   */
  public static String getFilename(URI uri) {
    final var scheme = uri.getScheme();
    if (Strings.isNullOrEmpty(scheme)) {
      return "";
    }
    if ("file".equals(scheme)) {
      if (uri.getPath().endsWith("/")) {
        return "";
      } else {
        return new java.io.File(uri.getPath()).getName();
      }
    } else if ("jar".equals(scheme)) {
      return getFilename(URI.create(uri.getSchemeSpecificPart()));
    } else if ("http".equals(scheme) || "https".equals(scheme)) {
      var path = uri.getPath();
      var p = path.lastIndexOf('/');
      if (p > -1) {
        return path.substring(p + 1);
      } else {
        return "";
      }
    } else {
      // You can try adding it above and see if it works...
      throw new IllegalArgumentException("TODO Add support for new URI scheme: " + uri);
    }
  }

  private URIs() {}
}
