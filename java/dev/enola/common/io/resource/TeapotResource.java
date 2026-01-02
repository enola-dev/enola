/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.net.MediaType;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Set;

/// TeapotResource 🫖 is an <a href="https://www.rfc-editor.org/rfc/rfc2324.html">RFC 2324</a>
/// inspired resource for <code>coffee:/</code> etc. URLs. It returns "I'm a teapot" (as UTF-8
/// encoded text; but NOT as <code>message/coffeepot</code> media type!). It ignores anything
/// written to it.
///
/// All international (§3.) coffee URI schemes are fully supported; and e.g.
/// `kafo://demo.enola.dev/pot-7?#syrup-type=Vanilla` is valid for sameideanoj. Please note
/// the following (major and breaking, sorry) backwards incompatibility: Due to what is assumed to
/// be a typo in the original RFC, for _Catalan, French and Galician_ the correctly accented URI
/// scheme `café` ("caf%C3%A9") instead of `cafè` ("caf%C3%E8") is used. However, we
/// do also support `cafè` as the _Italian_ URI scheme; this should help to avoid major
/// systems interoperability disasters. (It may still cause minor language issues; where full
/// interop with the original RFC spec is required, we recommend using a Locale override URL
/// parameter; e.g. `cafè://demo.enola.dev/pot-7?hl=fr`.)
///
/// Planned future features:
///
///   - Support multilingual (Locale) responses
///   - Teapot MCP A2A AI ML Agent (coming up!)
///   - For audio media type requests, return <a
///       href="https://en.wikipedia.org/wiki/I%27m_a_Little_Teapot">I'm a Little Teapot</a>
///   - For image media type requests, return a <a
///       href="https://en.wikipedia.org/wiki/Utah_teapot">Utah (Newell) teapot</a>
///   - <a href="https://www.rfc-editor.org/rfc/rfc7168.html">RFC 7168</a> support
///
///
/// @see <a
/// href="https://en.wikipedia.org/wiki/Hyper_Text_Coffee_Pot_Control_Protocol">Wikipedia</a>
/// @see <a href="https://save418.com/">save418.com</a>
public class TeapotResource extends BaseResource implements Resource {

    public static final Set<String> SCHEMES =
            ImmutableSet.of(
                    "koffie", "qæhvæ", "قهوة", "akeita", "koffee", "kahva", "kafe", "café", "咖啡",
                    "kava", "káva", "kaffe", "coffee", "kafo", "kohv", "kahvi", "Kaffee", "καφέ",
                    "कौफ़ी", "コーヒー", "커피", "кофе", "กาแฟ");

    private static final String CONTENT = "I'm a teapot";

    public static class Provider implements ResourceProvider {
        @Override
        public @Nullable Resource getResource(java.net.URI uri) {
            if (SCHEMES.contains(uri.getScheme())) {
                return new TeapotResource(uri);
            }
            return null;
        }
    }

    public TeapotResource(URI uri) {
        super(uri, MediaType.PLAIN_TEXT_UTF_8);
    }

    @Override
    public ByteSource byteSource() {
        return charSource().asByteSource(mediaType().charset().get());
    }

    @Override
    public CharSource charSource() {
        return CharSource.wrap(CONTENT);
    }

    @Override
    public ByteSink byteSink() {
        return NullByteSink.INSTANCE;
    }
}
