/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.html;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.ReadableResource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * HTML utilities.
 *
 * <p>Note that HTML to Thing conversion is done by TikaThingConverter, not here.
 */
public final class HTML {

    // TODO canonicalize JS inside <script>
    // TODO canonicalize CSS inside <style>

    public static String canonicalize(ReadableResource html, Charset outCharset, boolean format)
            throws IOException {
        var baseURI = URIs.getBase(html.uri());
        var dom = read(html);
        var outputSetting = new Document.OutputSettings();
        outputSetting.charset(outCharset);
        outputSetting.escapeMode(Entities.EscapeMode.xhtml);
        outputSetting.maxPaddingWidth(-1);
        if (format) {
            outputSetting.outline(true);
            outputSetting.indentAmount(2);
        } else {
            outputSetting.outline(false);
            outputSetting.indentAmount(0);
        }
        outputSetting.prettyPrint(true);
        dom.outputSettings(outputSetting);
        return dom.html();
    }

    public static Document read(ReadableResource html) throws IOException {
        var baseURI = URIs.getBase(html.uri());
        try (var is = html.byteSource().openBufferedStream()) {
            return Jsoup.parse(is, null, baseURI.toString());
        }
    }

    private HTML() {}
}
