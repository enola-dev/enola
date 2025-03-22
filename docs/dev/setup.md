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

## Documentation Writing

To work on documentation, launch:

* `tools/docs/serve-quick.bash` for hot reloading live refresh, which is great while writing (even though it has some limitations)
* `tools/docs/serve-build.bash` for a  "real" (full) docs build, without without the demo "screen cast" recordings (which are slow)
* `tools/docs/serve.bash` for generating the "real" (full) static `site/` exactly as it's deployed on <https://docs.enola.dev>

## Flox

[Please install Flox.dev](https://flox.dev/docs/install-flox) to work locally on this project, using its _virtual development environment._

This project's build scripts do not assume that Flox (Nix) is "activated" in your shell; they do this by themselves.
So once you have `flox` available on your `PATH`, launching e.g. `./test.bash` should _"just work"_ (please feel free to raise a bug if it does not).

To directly use tools installed into the Flox _virtual development environment_ from your shell (instead of **indirectly,** via the build scripts of this project), activate it in your shell using [something like this](https://github.com/vorburger/vorburger-dotfiles-bin-etc/blob/main/dotfiles/fish/functions/flox.fish), or e.g. `eval ...` (or `...| source` for Fish) [as described here](https://flox.dev/docs/tutorials/default-environment/#initial-setup). (We recommend this approach, instead of just using the alternative `flox activate`, like _Flox_ documentation suggests elsewhere, because it might preserve your own personal fancy shell customizations better - especially if you don't have `$SHELL` or `$FLOX_SHELL` set up correctly for an altnernative shell.)

TODO In IDEs? Like https://mise.jdx.dev/ide-integration.html#ide-plugins...

## Manual Tools Installation

!!! warning "Setup is in flux, with flox!"

    This project is in the process of adopting <https://flox.dev>. _The following is out of date!_

If you do still want to try, here's how to manually install what the development environment container comes built-in with:

1. Install Java Development Kit (JDK), [same version as in `.bazelrc`](//.bazelrc).
   There are different Java (like Linux) "distributions" (all based on OpenJDK).
   The easiest way to install one of them is typically to use your OS' package manager:

        sudo apt-get install openjdk-21-jdk openjdk-21-doc openjdk-21-source

   An alternative is to use e.g. [the SDKMAN!](https://sdkman.io)
   If you work on several projects using different Java versions,
   then we recommend using something like
   [jEnv (with `.java-version`)](https://www.jenv.be), or
   [asdf (with `.tool-versions`)](https://asdf-vm.com), or
   [direnv (with `.envrc`)](https://direnv.net).

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
        go install github.com/bazelbuild/bazelisk@latest

You should now be able to proceed as above (but without requiring _Docker)._

### Clean Up

Use `tools/flox` and do not directly do
`pip install -r requirements.txt` - because that script correctly uses [Flox](#flox).

In case of errors such as `ModuleNotFoundError: No module named 'pre_commit'`, try:

1. `rm -rf ~/.cache/pre-commit/`

In cases like `ImportError: cannot import name '...' from '...'`, maybe try:

1. `rm -rf ~/.local/lib/python*`

But if you are correctly in the virtual environment, there should be no need for this.

## Further Reading

You can now read more about:

* [Code Style](style.md)
* [IDE Support](ide.md)
* [Dependencies](dependencies.md)
* [Bazel](bazel.md)
