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

import com.google.common.io.ByteSource;

import io.ipfs.cid.Cid;

import java.io.IOException;

/**
 * BlobStore stores bytes, which can then be loaded again given their CID.
 *
 * <p>This interface could be implemented with:
 *
 * <ul>
 *   <li>just with simple non-distributed local files
 *   <li>Or into a Key Value Store
 *   <li>with <a href="https://github.com/systemd/casync/">systemd casync</a>
 *   <li>on <a href="https://ipfs.tech/">IPFS</a> in <a
 *       href="https://ipld.io/specs/transport/car/">CAR files</a>
 *   <li>on IPFS, distributed (with <a href="https://docs.ipfs.tech/install/command-line/">IPFS
 *       Bitswap with Kubo</a> (or <a href="https://github.com/Peergos/nabu">Nabu</a>)
 *   <li>on the <a href="https://iroh.network/">Iroh Network</a> based on <a
 *       href="https://www.iroh.computer/">Iroh</a> by <a href="https://n0.computer/">number0</a>
 *   <li>something else, maybe based on Consensus with Raft or Paxos.
 * </ul>
 */
public interface BlobStore {

    Cid store(ByteSource source) throws IOException;

    ByteSource load(Cid cid) throws IOException;

    // TODO void delete(Cid cid)
}
