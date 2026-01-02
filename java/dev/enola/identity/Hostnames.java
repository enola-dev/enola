/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.identity;

import static com.google.common.base.Strings.isNullOrEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class Hostnames {

    // TODO https://github.com/mattsheppard/gethostname4j
    // TODO Shell out (exec) to run "hostname", see https://stackoverflow.com/q/7348711/421602

    private static final Logger LOG = LoggerFactory.getLogger(Hostnames.class);

    public static final String LOCAL = local();

    private static String local() {
        String envHOSTNAME = System.getenv("HOSTNAME");
        if (!isNullOrEmpty(envHOSTNAME)) return envHOSTNAME;

        String envCOMPUTERNAME = System.getenv("COMPUTERNAME");
        if (!isNullOrEmpty(envCOMPUTERNAME)) return envCOMPUTERNAME;

        try {
            return InetAddress.getLocalHost().getCanonicalHostName();

        } catch (UnknownHostException e) {
            LOG.warn("UnknownHostException (defaulting to 'localhost')", e);
            return "localhost";
        }
    }

    private Hostnames() {}
}
