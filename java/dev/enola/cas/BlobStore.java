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

import com.google.common.io.ByteSource;

import io.ipfs.cid.Cid;

import java.io.IOException;

/**
 * BlobStore stores bytes, which can then be loaded again given their CID.
 *
 * <p>Note that this interface per-se does not specify anything about how this may be implemented...
 * just with simple non-distributed local files. Or into a Key Value Store. Or e.g. with <a
 * href="https://github.com/systemd/casync/">systemd casync</a>. Or may be on <a
 * href="https://ipfs.tech/>IPFS</a>, either in <a href="https://ipld.io/specs/transport/car/">CAR
 * files</a>, or it may be distributed (with <a
 * href="https://docs.ipfs.tech/install/command-line/">IPFS Bitswap with Kubo</a>, or <a
 * href="https://github.com/Peergos/nabu">Nabu</a>; or perhaps on the <a
 * href="https://iroh.network/">Iroh Network</a> based on <a
 * href="https://www.iroh.computer/">Iroh</a> by <a href="https://n0.computer/">number0</a>. Or
 * something else, maybe based on Consensus with Raft or Paxos.
 */
public interface BlobStore {

    Cid store(ByteSource source) throws IOException;

    ByteSource load(Cid cid) throws IOException;

    // TODO void delete(Cid cid)
}
