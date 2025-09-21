<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2025 The Enola <https://enola.dev> Authors

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

# Enola.dev 🕵🏾‍♀️ [![CI](https://github.com/enola-dev/enola/actions/workflows/ci.yaml/badge.svg)](https://github.com/enola-dev/enola/actions/workflows/ci.yaml) [![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-%E2%9C%85-grey)](https://conventionalcommits.org) [![Bazel Steward badge](https://img.shields.io/badge/Bazel_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,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)](https://github.com/VirtusLab/bazel-steward) [![Chat on Matrix](https://img.shields.io/matrix/enola.dev%3Amatrix.org)](https://matrix.to/#/#enola.dev:matrix.org)

[Enola](https://enola.dev) is a Graph-based analytics system for exploring relationships between common objects.

1. [Join group](https://groups.google.com/g/enoladev-announcements) <!-- TODO Later also create enola.dev-discuss@ --> and please ⭐ on GitHub
2. Read the [user guide](https://docs.enola.dev/use/) documentation
3. See [developer](https://docs.enola.dev/dev/setup/) docs and [Contribute](https://docs.enola.dev/contributing/)!
4. Comment on [issue #1649](https://github.com/enola-dev/enola/issues/1649) re. a chat room

This project is not an official Google project. It is not supported by
Google, and Google specifically disclaims all warranties as to its quality,
merchantability, or fitness for a particular purpose. ([`LICENSE`](LICENSE))

Use the _"Cite this repository"_ option on the right side of this page for citations.
It is important for us to be able to show the impact of our work in other projects and fields.

<!-- TODO [![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/enola-dev/enola?quickstart=1) -->

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=enola-dev/enola&type=Date)](https://www.star-history.com/#enola-dev/enola&Date)
