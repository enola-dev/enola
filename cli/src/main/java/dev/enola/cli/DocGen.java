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

import com.google.common.base.Strings;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.core.docgen.MarkdownDocGenerator;
import dev.enola.core.meta.EntityKindRepository;
import java.net.URI;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Spec;

@Command(name = "docgen", description = "Generate Markdown Documentation")
public class DocGen implements Callable<Integer> {

  @ParentCommand Enola enola;

  @Spec CommandSpec spec;

  @Override
  public Integer call() throws Exception {
    var model = enola.model;
    if (Strings.isNullOrEmpty(model)) {
      throw new CommandLine.ParameterException(spec.commandLine(), "Missing --model argument");
    }
    var modelResource = new ResourceProviders().getReadableResource(URI.create(model));

    var ekr = new EntityKindRepository();
    ekr.load(modelResource);

    // TODO replace getOut() with --output which defaults to PrintWriterResource but supports
    // URI
    new MarkdownDocGenerator().render(ekr, spec.commandLine().getOut());

    return 0;
  }
}
