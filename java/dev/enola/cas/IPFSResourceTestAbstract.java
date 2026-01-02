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
package dev.enola.cas;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import static java.nio.charset.StandardCharsets.UTF_8;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ResourceProvider;

import io.ipfs.cid.Cid.CidEncodingException;

import org.jspecify.annotations.Nullable;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public abstract class IPFSResourceTestAbstract {

    abstract @Nullable ResourceProvider getResourceProvider();

    public @Rule SingletonRule r1 = $(MediaTypeProviders.set());

    @Test
    @Ignore // TODO How to increase timeout in IPFS Kubo, so that this does not frequently fail CI?
    public void hello() throws IOException {
        if (getResourceProvider() == null) return;
        assertThat(bytesFromIPFS("ipfs://QmXV7pL1CB7A8Tzk7jP2XE9kRyk8HZd145KDptdxzmNLfu"))
                .isEqualTo("hello, world\n".getBytes(UTF_8));
    }

    @Test
    @Ignore // TODO How to increase timeout in IPFS Kubo, so that this does not frequently fail CI?
    public void vanGogh() throws IOException {
        var rp = getResourceProvider();
        if (rp == null) return;

        var url =
                "ipfs://bafybeiemxf5abjwjbikoz4mc3a3dla6ual3jsgpdr4cjr3oz3evfyavhwq/wiki/Vincent_van_Gogh.html";
        var r = getResourceProvider().get(url + "?mediaType=text/html;charset=UTF-8");
        assertThat(r.charSource().read()).startsWith("<html>");

        // TODO mediaType determination only works on (some?) public gateways,
        //  but not with local IPFS Desktop; note that we've hard-coded it above...
        // assertThat(r.mediaType()).isEqualTo(MediaType.HTML_UTF_8);
    }

    private byte[] bytesFromIPFS(String url) throws IOException {
        return getResourceProvider().get(url).byteSource().read();
    }

    @Test(expected = IllegalArgumentException.class)
    public void notIFPS() {
        var rp = getResourceProvider();
        if (rp == null) throw new IllegalArgumentException();

        var url = "http://www.google.com";
        new IPFSGatewayResource(URI.create(url), null, null);
    }

    @SuppressWarnings("unused")
    @Test(expected = CidEncodingException.class)
    public void badCID() throws IOException {
        var rp = getResourceProvider();
        if (rp == null) throw new CidEncodingException("");

        var url = "ipfs://bad";
        var unused = getResourceProvider().get(url).byteSource().read();
    }
}
