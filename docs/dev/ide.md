# IDE

## Visual Studio Code (VSC)

There are a lot of useful plugins for VSC; in particular for this project:

* [Bazel](https://marketplace.visualstudio.com/items?itemName=BazelBuild.vscode-bazel)
  but note that this is currently still very limited, and only syntax highlights `BUILD` files,
  but does not provide Java, [see these related notes](https://github.com/vorburger/LearningBazel/blob/develop/ToDo.md).

* [`.editorconfig`](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig)

* [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)

* For `*.proto` & `*.textproto`:
  * [proto3](https://marketplace.visualstudio.com/items?itemName=zxh404.vscode-proto3)
  * [TextProto](https://marketplace.visualstudio.com/items?itemName=thejustinwalsh.textproto-grammer)
  * [Clang Formatter](https://marketplace.visualstudio.com/items?itemName=Seaube.clangformat)

The Clang Formatter plugin requires the `clang-package` to be installed on the system:

* `sudo apt get install clang-format` (or `sudo dnf install clang-format`, or whatever package manager you use)

## JetBrains IntelliJ IDEA

Use for Java coding until Bazel integration in VSC is available.
