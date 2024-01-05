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

Eclipse is not actively supported by this project as an IDE. 

It may work using the [Bazel Eclipse Feature](https://github.com/salesforce/bazel-eclipse/blob/main/docs/bef/README.md), but this has not been tested yet.

We recommend using VSC.

## Visual Studio Code (VSC)

[The code style used in this project](style.md) can be enforced while typing using the following VSC plugins, which will automagically pick up their options from the respective configuration files in this repository:

* [`.editorconfig`](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig)

* For Bazel support:

      * [VSC Bazel](https://marketplace.visualstudio.com/items?itemName=BazelBuild.vscode-bazel) for syntax highlighting, linting and formatting `BUILD` files

      * [VSC Bazel for Java, by Salesforce](https://marketplace.visualstudio.com/items?itemName=sfdc.bazel-vscode-java) for Java support (it's **awesome,** and much better than [alternatives](https://github.com/vorburger/LearningBazel/blob/85aee3c956cbb84c8cd7d4f317be8ac36b62bad8/ToDo.md)). You may have to right-click on (the right "level" of) some folders to _Synchronize Projects with Bazel View_ to resolve weird initial problems like e.g. _Implicit super constructor Object() is undefined for default constructor. Must define an explicit constructor._ or _The import cannot be resolved._ or _cannot be resolved to a type_ or _The method is undefined for the type_ etc.

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

## Web IDE

In order to work on this project with a purely Web-based IDE, which is great to hack from e.g. a Tablet or Work Computer or some such setup,
we recommend you use [Visual Studio Code for the Web](https://code.visualstudio.com/docs/editor/vscode-web) at https://vscode.dev
together with a [Remote Tunnel](https://code.visualstudio.com/docs/editor/vscode-web#_use-your-own-compute-with-remote-tunnels)
e.g. to a computer at home, or a VM in the Cloud.

## JetBrains IntelliJ IDEA (IJ)

IJ can be used as a complete alternative to VSC, or only for Java coding, with:

* [Bazel](https://plugins.jetbrains.com/plugin/8609-bazel-for-intellij) from [ij.bazel.build](https://ij.bazel.build)
  (You [have to manually change](https://github.com/bazelbuild/intellij/issues/4693) the
  _Bazel Binary Location_ from the default `bazel` to `bazelisk`.)

* [Google Java Format](https://plugins.jetbrains.com/plugin/8527-google-java-format)

* [Bamboo Soy](https://plugins.jetbrains.com/plugin/9841-bamboo-soy) for `*.soy` templates

## Other

Some [people prefer coding using 🙊](https://en.wikipedia.org/wiki/Editor_war) `hexedit` or one its derivatives 😈 such as `vi` or `emacs`.

You can [just launch the `./test.bash` script to automatically format code](style.md) while coding locally when using other IDEs.

Or contribute to this page to document how to set up your favorite editor.
