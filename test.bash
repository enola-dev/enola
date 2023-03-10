#!/usr/bin/env bash
set -euo pipefail

tools/git/install-hooks.bash

# TODO build, not just test! (Because test targets may well not dependend on every build target.)
bazelisk test //...

# Check if https://pre-commit.com is available (and try to install it not)
if ! [ -x "$(command -v pre-commit)" ]; then
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
