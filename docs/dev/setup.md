<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2024 The Enola <https://enola.dev> Authors

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

**We highly recommend you use our ready-made [Web/Cloud IDE](ide.md) set-up.** _This page documents various tools you would need to locally install, but it may be out of date (please help to update it) - it's just so much easier to use a ready-made Web/Cloud IDE with only 1 click!_

1. Install Java Development Kit (JDK), [same version as in `.bazelrc`](../../.bazelrc).
   There are different Java (like Linux) "distributions" (all based on OpenJDK).
   The easiest way to install one of them is typically to use your OS' package manager:

       sudo apt-get install openjdk-21-jdk openjdk-21-doc openjdk-21-source

   An alternative is to use e.g. [the SDKMAN!](https://sdkman.io)
   If you work on several projects using different Java versions,
   then we recommend [using the great jEnv](https://www.jenv.be).

1. Install C/C++ etc. (it's required by the
   [Proto rule for Bazel](https://github.com/bazelbuild/rules_proto)), e.g. do:

       sudo apt-get install build-essential

1. Install [Python venv](https://docs.python.org/3/library/venv.html)
   (it's used by the presubmit and docs site generation), e.g. with:

       sudo apt-get install python3-venv

1. Install [Bazelisk](https://github.com/bazelbuild/bazelisk) (NOT Bazel),
   on a (recent enough...) Debian/Ubuntu [with Go](https://go.dev/doc/install)
   e.g. like this (or some more manual equivalent):

        sudo apt update
        sudo apt install golang-go

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
