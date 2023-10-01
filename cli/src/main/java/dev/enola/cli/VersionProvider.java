/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.cli;


import picocli.CommandLine;

// This is used by enola --version
public class VersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
        // TODO Read Git version generated during build, if available...
        return new String[] {
            Enola.DESCRIPTION,
            "@|yellow,italic Version: DEVELOPMENT|@",
            "@|red,bg(white),blink Copyright 2023 The Enola <https://enola.dev> Authors|@",
            "@|magenta,faint JVM: ${java.version} (${java.vendor} ${java.vm.name}"
                    + " ${java.vm.version})|@ on @|cyan,faint OS: ${os.name} ${os.version} ${os.arch}|@"
        };
        // TODO Add git repo? E.g. "https://github.com/enola-dev/enola/commits/main" or similar?
    }
}
