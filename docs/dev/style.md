# Code Style

This project automatically formats and checks ("lints") the style of its source code and configuration files, using:

* [EditorConfig](https://editorconfig.org) for all files, configured in an [`.editorconfig`](../../.editorconfig)
* [Google Java Style](https://google.github.io/styleguide/javaguide.html) with [`google-java-format`](https://github.com/google/google-java-format) for `*.java`
* [`clang-format`](https://clang.llvm.org/docs/ClangFormat.html) for `*.proto` and `*.textproto`
* [Buildifier](https://github.com/bazelbuild/buildtools/blob/master/buildifier/README.md) for `BUILD.bazel`
* [Markdownlint](https://github.com/DavidAnson/markdownlint) for `*.md`
* [Prettier](https://prettier.io) for YAML & JSON & HTML & CSS & JS & TS
* [ShellCheck](https://www.shellcheck.net) for `*.bash`
* _**TODO** for ASL License Headers_

There are [pre-commit.com](https://pre-commit.com) hooks for each of these tools configured in [`.pre-commit-config.yaml`](../../.pre-commit-config.yaml). (Only ShellCheck is directly invoked by Bazel.)

Run the [`./test.bash`](../../test.bash) script during local development to reformat changed files and re-run all tests affected by a change.

`.git/hooks/pre-commit` will also run the `./test.bash` locally for each of your `git commit` after you install it using `tools/git/install-hooks.bash`.

Contributions by Pull Requests on GitHub are tested with the same `./test.bash` which is launched by [this GitHub Action](../../.github/workflows/test.yaml).

In-IDE, the [plugins listed on our IDE page](ide.md) can be used.
