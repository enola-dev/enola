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
package dev.enola.common.secret;

import ch.vorburger.exec.ManagedProcessBuilder;
import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.exec.OutputStreamLogDispatcher;
import ch.vorburger.exec.OutputStreamType;

import com.google.errorprone.annotations.ThreadSafe;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.Optional;

/**
 * ExecPassSecretManager is an implementation of {@link SecretManager} which "shells out" (exec) to
 * [something like] <a href="https://www.passwordstore.org/"><tt>pass</tt></a>.
 */
@ThreadSafe
public class ExecPassSecretManager implements SecretManager {

    // TODO FIXME Suppress STDOUT logging!! :=((
    //   https://github.com/vorburger/ch.vorburger.exec/issues/272

    // TODO How to make this (pass) work from within Bazel tests? ENV vars, @TestOnly after all?!

    private static final Logger LOG = LoggerFactory.getLogger(ExecPassSecretManager.class);

    private static final OutputStreamLogDispatcher DO_NOT_LOG_STDOUT =
            new OutputStreamLogDispatcher() {
                @Override
                public Level dispatch(OutputStreamType type, String line) {
                    return switch (type) {
                        case STDOUT -> Level.TRACE; // Silence!!
                        case STDERR -> Level.DEBUG;
                    };
                }
            };

    private final boolean throwToDebug;

    public ExecPassSecretManager(boolean throwToDebug) {
        this.throwToDebug = throwToDebug;
    }

    public ExecPassSecretManager() {
        this(false);
    }

    @Override
    public Optional<Secret> getOptional(String key) {
        try {
            var proc =
                    new ManagedProcessBuilder("/usr/bin/env")
                            .addArgument("pass")
                            .addArgument(key)
                            .setOutputStreamLogDispatcher(DO_NOT_LOG_STDOUT)
                            .build()
                            .start()
                            .waitForExitMaxMsOrDestroy(7000);
            return Optional.of(new Secret(proc.getConsole().toCharArray()));
        } catch (ManagedProcessException e) {
            LOG.warn("Failed to get: {}", key, e);
            if (throwToDebug) throw new IllegalArgumentException("Failed to get: " + key, e);
            else return Optional.empty();
        }
    }

    @Override
    public void store(String key, char @Nullable [] value) {
        throw new UnsupportedOperationException("TODO Implemented store()");
        // When implementing, remember to Arrays.fill(value, '\0');
    }

    @Override
    public void delete(String key) {
        throw new UnsupportedOperationException("TODO Implemented delete()");
    }

    public static void main(String[] args) {
        new ExecPassSecretManager().getOptional("test").get().process(System.out::println);
    }
}
