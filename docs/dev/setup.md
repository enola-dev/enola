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

1. Install [Bazelisk](https://github.com/bazelbuild/bazelisk):

        go install github.com/bazelbuild/bazelisk@latest

1. Get the source code:

        git clone https://github.com/enola-dev/enola.git
        cd enola

1. Build everything and run the tests:

        ./test.bash

When tests are successful locally, a `.git/hooks/pre-commit` is installed.

You can now read more about:

* [Code Style](style.md)
* [IDE Support](ide.md)
* [Dependencies](dependencies.md)
* [Bazel](bazel.md)
