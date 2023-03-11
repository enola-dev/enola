# Dev Set-Up

To locally build, work on and contribute to this project, you need to:

1. Install some pre-requisites, including [Bazelisk](https://github.com/bazelbuild/bazelisk):

       go install github.com/bazelbuild/bazelisk@latest

1. Get the source code, and install its Git Hoooks:

       git clone https://github.com/vorburger/enola.git
       cd enola

1. Build everything and run the tests:

       ./test.bash

You can now [read more about Bazel's use in this project](bazel.md),
and then [set up your favorite IDE](ide.md).

PS: `./update.bash` automatically updates this project's external ("third-party") dependencies.
It should be regularly (manually) run by the active maintainers of this project.
