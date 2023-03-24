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

# Dev Set-Up

To locally build, work on and contribute to this project, you need to:

1. Install Java Development Kit (JDK) [version 11.0](../../.bazelrc).
   There are different Java (like Linux) "distributions" (all based on OpenJDK).
   The easiest way to install one of them is typically to use your OS' package manager:

       sudo apt-get install openjdk-11-jdk openjdk-11-doc openjdk-11-source

   An alternative is to use e.g. [the SDKMAN!](https://sdkman.io)
   If you work on several projects using different Java versions,
   then we recommend [using the great jEnv](https://www.jenv.be).

1. Install [Bazelisk](https://github.com/bazelbuild/bazelisk):

        go install github.com/bazelbuild/bazelisk@latest

1. Get the source code:

        git clone https://github.com/enola-dev/enola.git
        cd enola

1. Build everything and run the tests:

        ./test.bash

When tests are successful locally, a `.git/hooks/pre-commit` is installed.

To work on documentation, launch `tools/docs/serve.bash` for hot reloading live refresh which is great while writing
(even though it has some limitations), and `tools/docs/build.bash` for generating the "real" (full) static `site/`.

You can now read more about:

* [Code Style](style.md)
* [IDE Support](ide.md)
* [Dependencies](dependencies.md)
* [Bazel](bazel.md)
