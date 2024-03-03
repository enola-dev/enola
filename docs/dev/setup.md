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

## GitHub Codespaces

**We highly recommend you use our ready-made "1 click" [Web/Cloud IDE](ide.md) set-up.**

## Development Environment in a Docker Container

Because it can be a PITA to install all required tools, especially on non-Linux platforms,
this project comes with a containerized ("Docker") development environment, which you can use like this:

1. Get the source code:

        git clone https://github.com/enola-dev/enola.git
        cd enola

1. Build and enter (prompt) the Dev. Env. container, which includes all required tools, but with the source code "local / on host" mounted:

        ./devenv.bash

1. (You're now in the container.) Run the Enola CLI, built from source:

        ./enola

1. (You're still in the container.) Build everything and run the tests:

        ./test.bash

When tests ran fully successfully, then a `.git/hooks/pre-commit` that's useful for development is installed.

To work on documentation, launch `tools/docs/serve.bash` for hot reloading live refresh which is great while writing
(even though it has some limitations), and `tools/docs/build.bash` for generating the "real" (full) static `site/`.

## Manual Tools Installation

_This may be out of date (please help to update it) - it's just so much easier to use the above!_

If you do still want to try, here's how to manually install what the development environment container comes built-in with:

1. You do _NOT_ need to install a Java Development Kit (JDK), anymore.
   The [version downloaded by Bazel via `.bazelrc`](../../.bazelrc) is now used by all scripts.

1. Install C/C++ etc. (it's required by the
   [Proto rule for Bazel](https://github.com/bazelbuild/rules_proto)), e.g. do:

        sudo apt-get install build-essential

1. [Install Python venv](https://docs.python.org/3/library/venv.html)
   (it's used by the presubmit and docs site generation), e.g. with:

        sudo apt-get install python3-venv

1. You do _NOT_ need to install [Bazelisk](https://github.com/bazelbuild/bazelisk) (NOT Bazel),
   that, and other tools, will be automagically installed by script. You do however Go for those tools,
   and to build Enola's Go code.

1. [Install Go](https://go.dev/doc/install) e.g. like this on a (recent enough...) Debian/Ubuntu (or some more manual equivalent):

        sudo apt update
        sudo apt install golang-go

1. Now run `. .envrc` (or `source .envrc`, but NOT `./envrc`).
   You could automated this with [direnv](https://direnv.net) (also [for Nix](https://github.com/direnv/direnv/wiki/Nix), and [other alternatives](https://direnv.net/#related-projects)).

You should now be able to proceed as above (but without requiring _Docker),_ try specifically:

* `./enola`

* `./test.bash`

## Further Reading

You can now read more about:

* [Code Style](style.md)
* [IDE Support](ide.md)
* [Dependencies](dependencies.md)
* [Bazel](bazel.md)
