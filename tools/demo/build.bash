#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2026 The Enola <https://enola.dev> Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -euo pipefail

SCRIPT_DIR=$(realpath "$1")
SCRIPT=$(realpath "$SCRIPT_DIR/script")
TOOLS_DIR=$(realpath "$(dirname "$0")")
CWD=$(pwd)
cd "$SCRIPT_DIR"

if [ ! -f "$SCRIPT" ] || ! grep -q "asciinema-player" "$SCRIPT_DIR/index.md" 2>/dev/null; then
  exit 0
fi

# This script produces https://asciinema.org-like documentation from demo scripts!
# It uses the great https://github.com/zechris/asciinema-rec_script to achieve this.
# Possible alternatives, should we ever want any, include:
# - https://github.com/zechris/asciinema-rec_script
# - https://github.com/charmbracelet/vhs
# - https://github.com/faressoft/terminalizer
VERSION=7303a5256f3fdd77586834be1e3679bc520b78ea
URL=https://raw.githubusercontent.com/zechris/asciinema-rec_script/$VERSION/bin/asciinema-rec_script

BIN="$TOOLS_DIR"/../../.cache/demo/
mkdir -p "$BIN"
if ! [ -f "$BIN"/asciinema-rec_script ]; then
  curl -o "$BIN"/asciinema-rec_script $URL
fi
chmod +x "$BIN"/asciinema-rec_script

# This ensures that the demo script executes without failures before we record it.
# (asciinema-rec_script won't run it with "set -x" so it would not fail there.)
# TODO Make this actually work as intended (it does not, yet)...
# "$TOOLS_DIR"/test.bash "$SCRIPT_DIR"

CWD=$(pwd)
cd "$SCRIPT_DIR"

# Faster demo recording: minimize typing delays and countdown sleep
export SLEEP=${SLEEP:-1}
export PROMPT_PAUSE=${PROMPT_PAUSE:-0.2}
export TYPING_PAUSE=${TYPING_PAUSE:-0.001}

PATH="$TOOLS_DIR/../../bazel-bin/cli/:$PATH" \
  BEGIN_RECORDING="🎥 " END_RECORDING="🎬 " \
  "$BIN"/asciinema-rec_script "$SCRIPT"

# script.cast is directly played in docs via asciinema-player

# TODO Replace this, see https://github.com/zechris/asciinema-rec_script/issues/63
# NB: asciinema cat fails with "OSError: [Errno 6] No such device or address: '/dev/tty'"
# on GitHub Actions, see https://github.com/asciinema/asciinema/issues/548
# asciinema cat "$SCRIPT_DIR"/script.cast \
#   | perl -pe 's/\e([^\[\]]|\[.*?[a-zA-Z]|\].*?\a)//g' \
#   | col -b >"$SCRIPT_DIR"/script.output

cd "$CWD"
