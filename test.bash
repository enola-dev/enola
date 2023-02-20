#!/usr/bin/env bash
set -euxo pipefail

bazelisk test //...

# TODO mdlint *.md (ideally as Bazel sh_test, like shellcheck)
