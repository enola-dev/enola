#!/usr/bin/env bash
set -euox pipefail

# See docs/dev/bazel.md
bazelisk run @maven//:outdated

# shellcheck disable=SC1091
source ./.venv/bin/activate
pre-commit autoupdate
pre-commit clean
pre-commit gc
