#!/usr/bin/env bash
set -euo pipefail

DIR=$(realpath "$(dirname "$0")")

if ! [ -e "$DIR/../../.git/hooks/pre-commit" ]; then
  cp -v "$DIR/hooks/"* "$DIR/../../.git/hooks/"
fi
