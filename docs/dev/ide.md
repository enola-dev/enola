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

* For `*.proto` & `*.textproto`:
  * [proto3](https://marketplace.visualstudio.com/items?itemName=zxh404.vscode-proto3)
  * [TextProto](https://marketplace.visualstudio.com/items?itemName=thejustinwalsh.textproto-grammer)
  * [Clang Formatter](https://marketplace.visualstudio.com/items?itemName=Seaube.clangformat)

The Clang Formatter plugin requires the `clang-package` to be installed on the system:

* `sudo apt get install clang-format` (or `sudo dnf install clang-format`, or whatever package manager you use)

## JetBrains IntelliJ IDEA

Until Bazel integration in VSC is available, use IJ for Java coding, with:

* [Bazel](https://plugins.jetbrains.com/plugin/8609-bazel-for-intellij) from [ij.bazel.build](https://ij.bazel.build)

* [Google Java Format](https://plugins.jetbrains.com/plugin/8527-google-java-format)

## Other

Some [people prefer coding using 🙊](https://en.wikipedia.org/wiki/Editor_war) `hexedit` or one its derivatives 😈 such as `vi` or `emacs`.

You can [just launch the `./test.bash` script to automatically format code](style.md) while coding locally when using other IDEs.

Or contribute to this page to document how to set them up your favorite editor.
