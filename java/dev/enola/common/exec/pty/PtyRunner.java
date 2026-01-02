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
package dev.enola.common.exec.pty;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** PtyRunner executes a command in a PTY, connecting its I/O to an IS &amp; OS. */
public class PtyRunner implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(PtyRunner.class);

    // TODO Make non-public, and expose only via PtyProcessLauncher

    // TODO Rename class PtyRunner to something without *Runner

    private final PtyProcess process;
    private final OutputStream procOut;
    private final InputStream procIn;
    private final InputStream procErr;
    private final StreamPumper inPump;
    private final StreamPumper outPump;
    private final StreamPumper errPump;

    // TODO Support flipped InputStream/OutputStream? To avoid unnecessary pumping!

    public PtyRunner(
            boolean console,
            Path directory,
            String[] cmd,
            Map<String, String> env,
            InputStream in,
            OutputStream out,
            OutputStream err,
            boolean redirectErrorStream)
            throws IOException {

        var envCopy = new HashMap<>(env);
        if (!envCopy.containsKey("TERM")) envCopy.put("TERM", "xterm-256color");
        if (!envCopy.containsKey("COLORTERM")) envCopy.put("COLORTERM", "truecolor");

        process =
                new PtyProcessBuilder()
                        .setCommand(cmd)
                        .setEnvironment(envCopy)
                        .setConsole(!console)
                        .setDirectory(directory.toString())
                        // TODO .setInitialColumns(?).setInitialRows(?)
                        .setRedirectErrorStream(redirectErrorStream)
                        .start();

        procOut = process.getOutputStream();
        inPump = new StreamPumper("In", in, procOut);
        inPump.whenComplete(this::whenInPumpCompletes);

        procIn = process.getInputStream();
        outPump = new StreamPumper("Out", procIn, out);

        procErr = process.getErrorStream();
        errPump = new StreamPumper("Err", procErr, err);
    }

    Process process() {
        return process;
    }

    public void size(int columns, int rows) {
        process.setWinSize(new WinSize(columns, rows));
    }

    @Override
    public void close() {
        inPump.close();
        outPump.close();
        errPump.close();

        process.destroyForcibly();

        try {
            procIn.close();
            procOut.close();
            procErr.close();
        } catch (IOException e) {
            LOG.debug("Failed to close PTY child process I/O streams.", e);
        }
    }

    public int waitForExit() {
        return wait(null);
    }

    public int waitFor(Duration timeout) {
        return wait(timeout);
    }

    private int wait(@Nullable Duration timeout) {
        int exitCode;
        try {
            // NOT inPump.waitFor(timeout);

            boolean exited = false;
            if (timeout == null) {
                // Wait indefinitely
                process.waitFor();
                exited = true;
            } else {
                exited = process.waitFor(timeout.toNanos(), TimeUnit.NANOSECONDS);
            }

            // Wait for stream pumpers to finish, using the same timeout if provided
            // or a default/long timeout if the process was waited for indefinitely.
            Duration pumperTimeout = (timeout == null) ? StreamPumper.DEFAULT_TIMEOUT : timeout;
            outPump.waitFor(pumperTimeout);
            errPump.waitFor(pumperTimeout);

            if (exited) {
                exitCode = process.exitValue();
            } else {
                LOG.error("Failed to wait {} for process to exit; will destroyForcibly.", timeout);
                process.destroyForcibly();
                exitCode = -101;
            }

        } catch (InterruptedException e) {
            LOG.warn("Interrupted while waiting for PTY child process to terminate.", e);
            Thread.currentThread().interrupt(); // Restore interrupt status
            exitCode = -99;

        } finally {
            close();
        }
        return exitCode;
    }

    private void whenInPumpCompletes(Void result, @Nullable Throwable exception) {
        if (exception == null) {
            // The inPump task completed successfully; meaning EOF on InputStream!
            try {
                // Send EOF to the child process's stdin
                //   NOT procOut.close() BUT 0x04 for End of Transmission (EoT) AKA Ctrl-D:
                // TODO This works for the @Test cat() - but what if it's binary data?!
                procOut.write(4);
                procOut.flush();

            } catch (IOException e) {
                LOG.warn(
                        "Failed to close procOut (process's stdin) after inPump completion: {}",
                        e.getMessage(),
                        e);
            }
        } else {
            // The inPump task completed exceptionally (e.g., source stream
            // closed prematurely). procOut might already be closed by the exception's
            // context or will be handled by main close().
            LOG.warn(
                    "inPump task completed exceptionally. Not explicitly closing procOut here: {}",
                    exception.getMessage(),
                    exception);
        }
    }
}
