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
package dev.enola.cas;

import dev.enola.common.io.resource.OkHttpResource;
import dev.enola.common.io.resource.ResourceProvider;

public class IPFSGatewayResourceTest extends AbstractIPFSResourceTest {

    // See https://docs.enola.dev/use/fetch/#ipfs
    private static final String IPFS_GATEWAY = "https://dweb.link/ipfs/";

    private static final ResourceProvider httpResourceProvider = new OkHttpResource.Provider();

    @Override
    protected ResourceProvider getResourceProvider() {
        return new IPFSGatewayResource.Provider(httpResourceProvider, IPFS_GATEWAY);
    }
}
