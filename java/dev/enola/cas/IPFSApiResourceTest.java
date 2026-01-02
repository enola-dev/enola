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

import dev.enola.common.Net;
import dev.enola.common.io.resource.ResourceProvider;

import io.ipfs.api.IPFS;

import org.jspecify.annotations.Nullable;

public class IPFSApiResourceTest extends IPFSResourceTestAbstract {

    @Override
    protected @Nullable ResourceProvider getResourceProvider() {
        if (!Net.portAvailable(5001)) return null;
        var ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        return new IPFSApiResource.Provider(new IPFSBlobStore(ipfs));
    }
}
