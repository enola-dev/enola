#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

# Because mcr.microsoft.com/devcontainers/universal:2-linux
# comes with several Java versions pre-installed by SDKMAN already,
# let's wipe everything, and (re)install only the one we want below
# (via ASDF, for consistency), to avoid confusion:
# TODO Remove this when .devcontainer/devcontainer.json switched to a ligher base image
rm -rf /usr/local/sdkman/candidates/java/

if ! [ -x "$(command -v asdf)" ]; then
  if ! [ -d "$HOME/.asdf/" ]; then
    # As per https://asdf-vm.com/guide/getting-started.html
    # Keep the --branch version in sync with //.github/workflows/ci.yaml
    git clone https://github.com/asdf-vm/asdf.git ~/.asdf --branch v0.14.0

    # shellcheck disable=SC2016
    echo '. "$HOME/.asdf/asdf.sh"' >> ~/.bashrc
    # shellcheck disable=SC2016
    echo '. "$HOME/.asdf/completions/asdf.bash" ' >> ~/.bashrc

    mkdir -p ~/.config/fish/completions
    echo 'source ~/.asdf/asdf.fish' >> ~/.config/fish/config.fish
    ln -s ~/.asdf/completions/asdf.fish ~/.config/fish/completions

  fi
  # shellcheck source=/dev/null
  . "$HOME/.asdf/asdf.sh"
fi

asdf info
asdf plugin add golang
asdf plugin add java
asdf plugin add protoc
# This installs the tools as per //.tools-versions (with fixed versions)
asdf install
asdf current

go version
java -version
protoc --version
