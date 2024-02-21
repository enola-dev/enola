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

# IDE

Ensure you are [set-up](setup.md) with a working build on the CLI before configuring your IDE.

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
The [bazel-vscode-java issues #94](https://github.com/salesforce/bazel-vscode-java/issues/94) has more background about this.

## Local Visual Studio Code (VSC)

You must manually install the following tools locally for all VSC extensions to work:

```bash
$ go install github.com/yoheimuta/protolint/cmd/protolint@latest

$ sudo apt get install clang-format
# sudo dnf install clang-format
```

[The code style used in this project](style.md) will be enforced while typing in VSC using the extensions below. They are automatically proposed to be installed (because they are all listed in [`.vscode/extensions.json`](https://github.com/enola-dev/enola/blob/main/.vscode/extensions.json)). They will all also automagically pick up their options from the respective configuration files in this repository; this makes the VSC IDE and pre-commit use the same parameters for these tools.

* [`.editorconfig`](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig) for normalized line endings etc. on all files

* [VSC Bazel](https://marketplace.visualstudio.com/items?itemName=BazelBuild.vscode-bazel) for syntax highlighting, linting and formatting `BUILD` files

* [VSC Bazel for Java, by Salesforce](https://marketplace.visualstudio.com/items?itemName=sfdc.bazel-vscode-java) for Java support (it's **awesome,** and much better than [alternatives](https://github.com/vorburger/LearningBazel/blob/85aee3c956cbb84c8cd7d4f317be8ac36b62bad8/ToDo.md)). Check out its [great Troubleshooting Guide](https://github.com/salesforce/bazel-vscode-java/blob/main/docs/troubleshoot.md) in case of any set-up problems.

* [google-java-format](https://marketplace.visualstudio.com/items?itemName=JoseVSeb.google-java-format-for-vs-code)

* [markdownlint](https://marketplace.visualstudio.com/items?itemName=DavidAnson.vscode-markdownlint)

* [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)

* [protolint](https://marketplace.visualstudio.com/items?itemName=Plex.vscode-protolint) for `*.proto` validation (this requires `protolint`, see above)

* [proto3](https://marketplace.visualstudio.com/items?itemName=zxh404.vscode-proto3) for `*.proto` editing

* [TextProto](https://marketplace.visualstudio.com/items?itemName=thejustinwalsh.textproto-grammer) for `*.textproto` syntax highlighting

* [Clang Formatter](https://marketplace.visualstudio.com/items?itemName=Seaube.clangformat) (this requires `clang-package`, see above) for `*.textproto` formatting

## JetBrains IntelliJ IDEA (IJ)

IJ can be used as a complete alternative to VSC, or only for Java coding, with:

* [Bazel](https://plugins.jetbrains.com/plugin/8609-bazel-for-intellij) from [ij.bazel.build](https://ij.bazel.build)
  (You [have to manually change](https://github.com/bazelbuild/intellij/issues/4693) the
  _Bazel Binary Location_ from the default `bazel` to `bazelisk`.)

* [Google Java Format](https://plugins.jetbrains.com/plugin/8527-google-java-format)

## Eclipse

Eclipse is not actively supported by this project as an IDE.

It may work using the [Bazel Eclipse Feature](https://github.com/salesforce/bazel-eclipse/blob/main/docs/bef/README.md), but this has not been tested yet.

We recommend using VSC.

## Other

Some [people prefer coding using 🙊](https://en.wikipedia.org/wiki/Editor_war) `hexedit` or one its derivatives 😈 such as `vi` or `emacs`.

You can [just launch the `./test.bash` script to automatically format code](style.md) while coding locally when using other IDEs.

Or contribute to this page to document how to set up your favorite editor.
