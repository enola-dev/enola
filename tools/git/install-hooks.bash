#!/usr/bin/env bash
set -euo pipefail

DIR=$(realpath "$(dirname "$0")")

cp -v "$DIR/hooks/"* "$DIR/../../.git/hooks/"
