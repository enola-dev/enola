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

# https://sdkman.io/usage
# This script activates the Java 21 distribution which the
# Java feature in .devcontainer/devcontainer.json installed.
# TODO Raise bug why this doesn't just work out of the box...
# TODO Replace this with asdf! More uniform.

# Because mcr.microsoft.com/devcontainers/universal:2-linux
# comes with several Java versions pre-installed already,
# let's wipe everything, and install the one we want,
# to avoid confusion:
rm -rf /usr/local/sdkman/candidates/java/

chmod +x /usr/local/sdkman/bin/sdkman-init.sh
source /usr/local/sdkman/bin/sdkman-init.sh

# Java version must match .bazelrc
sdk install java 21-tem
sdk use java 21-tem
java -version

# Wipe out .NET SDK (if installed), because it "shadows" SDKMAN on PATH.
rm -rf /home/codespace/.dotnet/sdk
