#!/usr/bin/env bash
set -euxo pipefail

tools/git/install-hooks.bash

bazelisk test //...

# TODO mdlint *.md (ideally as Bazel sh_test, like shellcheck)
