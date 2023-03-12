<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023 The Enola <https://enola.dev> Authors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->

# Enola 🕵🏾‍♀️ ToDo

1. Enforce https://www.conventionalcommits.org like git commit messages
   starting with feat/model/fix/build/docs/clean/format/refactor: and core/k8s/tools:
   using https://github.com/jorisroovers/gitlint

1. https://github.com/tcort/markdown-link-check/ via pre-commit as shown e.g. on https://github.com/pre-commit/pre-commit/issues/2687
   (And contribute a PR to the project to illustrate how to use it on the README.)

1. Replace `shellcheck` with a pre-commit check instead of running it in Bazel
1. Abandon `build.bash` by running script as a `sh_test` in Bazel with `docs/**` + `mkdocs.yaml` as (only) `srcs`
1. docs: `serve.bash` with `mkdocs server` AND Bazel "watch" (?) to rebuild Proto MD on change of `.proto`

1. Implement QueryAvailableEntities, based on the model
1. connectors/demo/ with test to illustrate the Service to get IDs
1. CLI with PicoCLI for `enola-demo be OK foo:a`
1. CLI completion - hard or trivial?

1. K8s MVP Proto

    - `ownerReferences`

1. Read [RFC 6570](https://www.rfc-editor.org/rfc/rfc6570) and integrate https://github.com/fge/uri-template for QueryParameter#ref_id_template

1. Put MVP (!) screenshot image on README (and make README very short; like e.g. https://github.com/jorisroovers/gitlint's)
   See https://squidfunk.github.io/mkdocs-material/reference/images/
   With https://squidfunk.github.io/mkdocs-material/reference/grids/

1. Use GitHub Projects & Roadmap to create The Plan, and use Trello-like Kanban board view with ToDo, WIP, Done.

1. https://unifiedjs.com ?

1. LinuxMachine MVP; incl. dmsg Kernel Log analyzer, https://github.com/prometheus/node_exporter reader, etc.

1. https://squidfunk.github.io/mkdocs-material/setup/setting-up-site-analytics/

1. Add to https://github.com/SquadcastHub/awesome-sre-tools

1. https://plugins.jetbrains.com/plugin/10456-prettier ? Or no longer needed, with VSC?
1. [Bazel Java IDE support](https://github.com/vorburger/LearningBazel/blob/develop/ToDo.md) in VSC instead IJ?!
1. [`google-java-format`](https://github.com/google/google-java-format) with VSC:

    - https://www.sethvargo.com/using-google-java-format-with-vs-code/
    - https://marketplace.visualstudio.com/search?term=google-java-format&target=VSCode&category=All%20categories&sortBy=Relevance

1. [Dependabot](https://github.com//dependabot-core/issues/2196), or Renovate

1. Eventually switch from Java 11 to probably Java 19 (in `.bazelrc`)

1. https://gerrit-review.googlesource.com/Documentation/dev-intellij.html#_copyright ?

1. https://github.com/textlint/textlint with https://github.com/textlint/textlint/wiki/Collection-of-textlint-rule
