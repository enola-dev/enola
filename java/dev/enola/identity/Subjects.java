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
package dev.enola.identity;

import dev.enola.thing.java.TBF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Subjects {
    private static final Logger LOG = LoggerFactory.getLogger(Subjects.class);

    private final TBF tbf;
    private final Subject local;

    public Subjects(TBF tbf) {
        this.tbf = tbf;

        String userlabel;
        String username = System.getProperty("user.name");
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
            userlabel = username + "@" + hostname;
        } catch (UnknownHostException e) {
            LOG.warn("InetAddress.getLocalHost().getHostName() failed", e);
            hostname = "";
            userlabel = username;
        }

        this.local =
                tbf.create(Subject.Builder.class, Subject.class)
                        .iri("subject://" + hostname + "/" + username)
                        .label(userlabel)
                        .build();
    }

    public Subject local() {
        return local;
    }
}
