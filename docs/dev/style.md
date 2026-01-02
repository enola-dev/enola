<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2026 The Enola <https://enola.dev> Authors

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

# Code Style

This project automatically formats and checks ("lints") the style of its source code and configuration files, using:

* [EditorConfig](https://editorconfig.org) for all files, configured in an [`.editorconfig`](//.editorconfig)
* [Google Java Style](https://google.github.io/styleguide/javaguide.html) with [`google-java-format`](https://github.com/google/google-java-format) for `*.java`
* [protolint](https://github.com/yoheimuta/protolint) for `*.proto` semantics
* [clang-format](https://clang.llvm.org/docs/ClangFormat.html) for `*.proto` and `*.textproto` format
* [Buildifier](https://github.com/bazelbuild/buildtools/blob/master/buildifier/README.md) for `BUILD.bazel`
* [DavidAnson/Markdownlint](https://github.com/DavidAnson/markdownlint)[^1] for `*.md`
* [Prettier](https://prettier.io) for YAML & JSON & HTML & CSS & JS & TS (but not MD)
* [ShellCheck](https://www.shellcheck.net) for `*.bash`
* [Lucas-C/pre-commit-hooks](https://github.com/Lucas-C/pre-commit-hooks) to add missing license headers

There are [pre-commit.com](https://pre-commit.com) hooks for each of these tools configured in [`.pre-commit-config.yaml`](//.pre-commit-config.yaml).

Run the [`./test.bash`](//test.bash) script during local development to reformat changed files and re-run all tests affected by a change.

`.git/hooks/pre-commit` will also run the `./test.bash` locally for each of your `git commit` after you locally install it using `tools/git/install-hooks.bash`.

Contributions by Pull Requests on GitHub are tested with the same `./test.bash` which is launched by [this GitHub Action](//.github/workflows/ci.yaml).

In-IDE, the [plugins listed on our IDE page](ide.md) can be used.

[^1]: DavidAnson/Markdownlint is not to be confused with [markdownlint/markdownlint](https://github.com/markdownlint/markdownlint) which was originally the inspiration for DavidAnson/Markdownlint but is now an unrelated separate project now has slightly different configuration.
