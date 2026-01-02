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
package dev.enola.chat.sshd;

import org.apache.sshd.common.file.nonefs.NoneFileSystemFactory;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.server.ServerBuilder;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ShellFactory;
import org.jline.builtins.ssh.ShellFactoryImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

public class EnolaSshServer implements AutoCloseable {

    private final SshServer server;

    public EnolaSshServer(int port, Path hostKeyStorePath) throws IOException {
        this(port, hostKeyStorePath, new ShellFactoryImpl(ChatShell::new));
    }

    public EnolaSshServer(int port, Path hostKeyStorePath, ShellFactory shellFactory)
            throws IOException {
        var builder = ServerBuilder.builder();

        // NOTA BENE: Good God, no local file system access for Enola Chat over SSH!
        builder.fileSystemFactory(NoneFileSystemFactory.INSTANCE);

        server = builder.build();
        if (port != 0) server.setPort(port);

        // TODO SimpleGeneratorHostKeyProvider vs. BouncyCastleGeneratorHostKeyProvider ?!
        // NOTA BENE: We use Ed25519 instead of the (default) ecdsa-sha2-nistp521 here;
        //   see also https://github.com/apache/mina-sshd/issues/747 for some related background.
        hostKeyStorePath.getParent().toFile().mkdirs();
        var keyPairProvider = new SimpleGeneratorHostKeyProvider(hostKeyStorePath);
        keyPairProvider.setAlgorithm("Ed25519");
        server.setKeyPairProvider(keyPairProvider);

        // NOTA BENE: For Enola Chat, we just accept all public keys!
        var pubKeyAuthenticator = new NonLoggingAcceptAllPublickeyAuthenticator();
        server.setPublickeyAuthenticator(pubKeyAuthenticator);

        server.setShellFactory(shellFactory);

        // https://github.com/apache/mina-sshd/blob/master/docs/server-setup.md#providing-server-side-heartbeat
        server.setSessionHeartbeat(
                SessionHeartbeatController.HeartbeatType.IGNORE, Duration.ofSeconds(7));

        server.start();
    }

    public int port() {
        return server.getPort();
    }

    @Override
    public void close() throws Exception {
        // NB: TODO Is server.close(); or server.stop(); correct?
        server.stop(true);
    }
}
