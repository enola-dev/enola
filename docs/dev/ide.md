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

# IDE

## Eclipse

Eclipse is not actively supported by this project as an IDE. We recommend using VSC.

## Visual Studio Code (VSC)

[The code style used in this project](style.md) can be enforced while typing using the following VSC plugins, which will automagically pick up their options from the respective configuration files in this repository:

* [`.editorconfig`](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig)

* [Bazel](https://marketplace.visualstudio.com/items?itemName=BazelBuild.vscode-bazel)
  but note that this is currently still very limited, and only syntax highlights `BUILD` files,
  but does not provide Java, [see these related notes](https://github.com/vorburger/LearningBazel/blob/develop/ToDo.md).

* [google-java-format](https://marketplace.visualstudio.com/search?term=google-java-format&target=VSCode&category=All%20categories&sortBy=Relevance) TODO pick which one of the 8 to use 😸

* [markdownlint](https://marketplace.visualstudio.com/items?itemName=DavidAnson.vscode-markdownlint)

* [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)

* [protolint](https://github.com/plexsystems/vscode-protolint), which requires `protolint`:

        go install github.com/yoheimuta/protolint/cmd/protolint@latest

* For `*.proto` & `*.textproto`:

      * [proto3](https://marketplace.visualstudio.com/items?itemName=zxh404.vscode-proto3)
      * [TextProto](https://marketplace.visualstudio.com/items?itemName=thejustinwalsh.textproto-grammer)
      * [Clang Formatter](https://marketplace.visualstudio.com/items?itemName=Seaube.clangformat), which requires `clang-package`:

            sudo apt get install clang-format
            # sudo dnf install clang-format

## JetBrains IntelliJ IDEA

Until Bazel integration in VSC is available, use IJ for Java coding, with:

* [Bazel](https://plugins.jetbrains.com/plugin/8609-bazel-for-intellij) from [ij.bazel.build](https://ij.bazel.build)

* [Google Java Format](https://plugins.jetbrains.com/plugin/8527-google-java-format)

* [Bamboo Soy](https://plugins.jetbrains.com/plugin/9841-bamboo-soy) for `*.soy` templates

## Other

Some [people prefer coding using 🙊](https://en.wikipedia.org/wiki/Editor_war) `hexedit` or one its derivatives 😈 such as `vi` or `emacs`.

You can [just launch the `./test.bash` script to automatically format code](style.md) while coding locally when using other IDEs.

Or contribute to this page to document how to set them up your favorite editor.
