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
package dev.enola.common.exec;

/**
 * ProcessLauncher launches a process described by a {@link ProcessRequest}.
 *
 * <p>Implementations could include:
 *
 * <ul>
 *   <li>JDK's built-in {@link ProcessBuilder}
 *   <li>Wrappers around native bindings (e.g. <a
 *       href="https://github.com/JetBrains/pty4j">pty4j</a> (in a "raw" JLine Terminal), or <a
 *       href="https://github.com/brettwooldridge/NuProcess">NuProcess</a>, or <a
 *       href="https://github.com/jnr/jnr-process">jnr-process</a>)
 *   <li>SSH client
 *   <li>in Container
 *   <li>in VM
 * </ul>
 *
 * {@link Exec} is a related higher-level utility which builds upon this that may also interest you.
 */
public interface ProcessLauncher {

    // TODO Upstream this into https://github.com/vorburger/ch.vorburger.exec eventually

    // TODO Implement atop an SSH client; like Apache MINA, or JSch, or jcabi-ssh

    // TODO Implement running in an "environment" container and/or VM, local or remote

    /**
     * Launch a new process (and immediately return).
     *
     * <p>Note that this intentionally does NOT throw IOException. Any start errors, such as a
     * failure to launch e.g. due to an image file not found or not executable, as well as
     * implementation-specific conditions encountered later (such as perhaps an "unexpected return
     * code") are always only reported via {@link ProcessResponse#async()}.
     *
     * @param request the {@link ProcessRequest}
     * @return the {@link ProcessResponse}
     */
    ProcessResponse execute(ProcessRequest request);
}
