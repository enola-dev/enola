#!/usr/bin/env bash
set -euxo pipefail

bazelisk test //...

# TODO find shellcheck *.bash
# TODO mdlint *.md
# TODO shellcheck and mdlint as Bazel sh_binary or sh_test
