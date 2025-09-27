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

# IDE

Ensure you are [set up](setup.md) with a working build on the CLI before configuring your IDE.

Enter `nix develop` and run `./test.bash` to verify that the setup is correct; it should complete without errors.

Because of [the Nix requirement](setup.md), please launch your IDE not via its usual graphical Desktop launch, but
from within a `nix develop` shell on the CLI, where `PATH` is the _Nix_ environment. This avoids many problems, like
[issue #1657](https://github.com/enola-dev/enola/issues/1657), etc.

## JetBrains IntelliJ IDEA (IJ)

Do not launch IDEA graphically via Toolbox, but from the CLI. To do this, start JetBrains Toolbox, click on Settings of IJ
(in Toolbox, BEFORE starting it), and `Configure...` the _shell scripts' location_ to be a directory on your `PATH` (e.g., `$HOME/.local/bin`).
Toolbox may warn that it _Cannot find the specified directory in the system PATH_.
You can ignore this, as long as the directory is on the `PATH` in the `nix develop` shell from which you will launch IJ.
Toolbox will now have put a launch script into that directory.

The [required plugins](https://www.jetbrains.com/help/idea/managing-plugins.html#required-plugins) are managed via the [`.idea/externalDependencies.xml`](https://github.com/enola-dev/enola/blob/main/.idea/externalDependencies.xml).

## Visual Studio Code (VSC)

Please launch VSC via `code .` in the `nix develop` environment, not via another starting mechanism (see the introduction of this page).

This is not using Bazel IDE integration (anymore now), but just simple "[unmanaged](https://code.visualstudio.com/docs/java/java-project#_manage-dependencies-for-unmanaged-folder) source folders", configured in `.vscode/settings.json`. Please let us know if you find any discrepancies with the build system.

The required VSC extensions are managed via [`.vscode/extensions.json`](https://github.com/enola-dev/enola/blob/main/.vscode/extensions.json). They will automagically pick up their options from the respective configuration files in this repository; this makes the VSC IDE and pre-commit use the same parameters for these tools.

The JAR libraries in the `generated/classpath` directory are placed there by the `tools/javac/classpath.bash` script.

## Eclipse

Eclipse is not actively supported by this project as an IDE.

It may work using the [Bazel Eclipse Feature](https://github.com/salesforce/bazel-eclipse/blob/main/docs/bef/README.md), but this has not been tested yet.

We recommend using VSC.

<!-- TODO Other IDEs than JetBrains IntelliJ IDEA (IJ) don't actually currently really work for working on the Java code...

## GitHub Codespaces

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/enola-dev/enola?quickstart=1)

Web-based IDEs are great to easily work from any computer.

We recommend you use GitHub Codespaces to contribute to this project by [clicking here](https://codespaces.new/enola-dev/enola?quickstart=1).

This project is configured to automatically configure your Codespace with all required tools. If anything doesn't just work "out of the box", please [create an issue](https://github.com/enola-dev/enola/issues). The only "it's not 100% fully automated" currently known open issues to be aware of are:

1. You must _"Switch to Pre-Release Version"_ for the _Bazel extension for Java_ extension

If you are missing your fancy custom Shell configuration that you have built over the last 100 years,
you should [set up your dotfiles for Codespaces](https://docs.github.com/en/codespaces/setting-your-user-preferences/personalizing-github-codespaces-for-your-account#dotfiles)
such as [e.g. this dude did in his dotfiles](https://github.com/vorburger/vorburger-dotfiles-bin-etc#github-codespaces).

If you are hitting the _"Codespace could not be created: SKU name 'basicLinux32gb' is not allowed for this repository"_
error, this actually (somewhat confusingly) indicates a _billing_ problem. Please create an issue on the project to
discuss how we can increase quota so that you can contribute.

## Visual Studio Code on Web

You may alternatively be interested in using:

* [Visual Studio Code for the Web](https://code.visualstudio.com/docs/editor/vscode-web) at <https://vscode.dev>
* [The `github.dev` web-based editor](https://docs.github.com/en/codespaces/the-githubdev-web-based-editor)

with a [Remote Tunnel](https://code.visualstudio.com/docs/editor/vscode-web#_use-your-own-compute-with-remote-tunnels)
e.g. your own VM in the Cloud, or to a computer at home, or perhaps even simply running and exposing a `code serve-web`
with your own VPN or SSH port forwarding solution (but without GitHub tunnel).

While GitHub Codespaces are built on VSC Web technologies, this project is currently known
to unfortunately not work well with such non-GitHub Codespaces VSC Web environments.
The [bazel-vscode-java issue #94](https://github.com/salesforce/bazel-vscode-java/issues/94) has more background about this.

-->

## Debug

    bazelisk run //java/dev/enola/chat:demo  -- --debug
    (...)
    Listening for transport dt_socket at address: 5005

## Other

Some [people prefer coding using 🙊](https://en.wikipedia.org/wiki/Editor_war) `hexedit` or one its derivatives 😈 such as `vi` or `emacs`.

You can [just launch the `./test.bash` script to automatically format code](style.md) while coding locally when using other IDEs.

Or contribute to this page to document how to set up your favorite editor.
