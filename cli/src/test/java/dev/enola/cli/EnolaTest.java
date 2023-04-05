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

import static dev.enola.cli.CommandLineSubject.assertThat;
import static dev.enola.cli.Enola.cli;

import org.junit.Ignore;
import org.junit.Test;

public class EnolaTest {

  // TODO testVerbose()

  @Test
  public void testNoArguments() {
    assertThat(cli()).hasExitCode(2).err().startsWith("Missing required subcommand");
  }

  @Test
  public void testBadArgument() {
    assertThat(cli("--bad")).hasExitCode(2).err().startsWith("Unknown option: '--bad'");
  }

  @Test
  public void testHelp() {
    assertThat(cli("-h")).hasExitCode(0).out().startsWith("Usage: enola [-hVv]");
    assertThat(cli("--help")).hasExitCode(0).out().startsWith("Usage: enola [-hVv]");
  }

  @Test
  public void testVersion() {
    assertThat(cli("-V")).hasExitCode(0).out().contains("Copyright");
    assertThat(cli("--version")).hasExitCode(0).out().contains("Copyright");
    // TODO assertThat(cli("version")).hasExitCode(0).err().contains("Copyright");
  }

  @Test
  @Ignore // TODO Implement... must extract demo-model.textproto, or support classpath: URI scheme
  // in ResourceProviders
  public void testDocGen() {
    assertThat(cli("docgen")).hasExitCode(0).out().isEqualTo("hi\n");
    // TODO Assert that docgen actually did work...
  }
}
