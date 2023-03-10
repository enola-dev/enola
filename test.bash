#!/usr/bin/env bash
set -euo pipefail

if ! [ -x "$(command -v bazelisk)" ]; then
    echo "bazelisk is not installed, please run e.g. 'go install github.com/bazelbuild/bazelisk@latest' or an equivalent from https://github.com/bazelbuild/bazelisk#installation or see docs/dev/setup.md"
    exit 255
fi

# TODO build, not just test! (Because test targets may well not dependend on every build target.)
bazelisk test //...

# Check if https://pre-commit.com is available (and try to install it not)
if ! [ -e "./.venv/bin/pre-commit" ]; then
  echo "https://pre-commit.com is not available..."

  if ! [ -x "$(command -v python3)" ]; then
    echo "python3 is not installed, please run e.g. 'sudo apt-get install virtualenv python3-venv' (or an equivalent)"
    exit 255
  fi

  if ! [ -d ./.venv/ ]; then
    python3 -m venv .venv
  fi
  # shellcheck disable=SC1091
  source ./.venv/bin/activate
  pip install pre-commit
else
  # shellcheck disable=SC1091
  source ./.venv/bin/activate
fi

pre-commit run

# TODO mdlint *.md (ideally as Bazel sh_test, like shellcheck)

# This makes sure that this test.bash will run as a pre-commit hook
# NB: We DO NOT want to "pre-commit install" because that won't run bazelisk!
# (And because our own venv etc. stuff above is better for the "first touch" contributor experience.)
tools/git/install-hooks.bash
