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

THIS="$(dirname "$(realpath "$0")")"
ROOT="$THIS"/../..

rm -rf /tmp/enola/distro/jpackage
mkdir -p /tmp/enola/distro/jpackage/in
cp "$ROOT"/bazel-bin/java/dev/enola/cli/enola_deploy.jar /tmp/enola/distro/jpackage/in/
mkdir -p /tmp/enola/distro/jpackage/out

# TODO https://github.com/enola-dev/enola/issues/748: --generate-cds-archive
jlink --output /tmp/enola/distro/jpackage/out/jlink --include-locales=en \
  --module-path /usr/lib/jvm/java-21-openjdk/jmods --add-modules java.xml,jdk.xml.dom,jdk.zipfs,java.base,jdk.crypto.ec,jdk.unsupported,java.logging,jdk.crypto.cryptoki,jdk.net,jdk.random,jdk.internal.ed,java.net.http,jdk.internal.opt,jdk.internal.le,jdk.localedata \
  --strip-native-commands --strip-debug --no-man-pages --no-header-files

jpackage --verbose --type app-image \
  --runtime-image /tmp/enola/distro/jpackage/out/jlink \
  --input /tmp/enola/distro/jpackage/in \
  --main-class dev.enola.cli.EnolaApplication --main-jar enola_deploy.jar \
  --app-version 0.0.1 --copyright "Copyright 2023-2025 The Enola Authors" \
  --description "See https://docs.enola.dev" \
  --dest /tmp/enola/distro/jpackage/out -n enola
tar czf site/download/latest/enola.x86_64.tgz -C /tmp/enola/distro/jpackage/out/ enola
ls -lh site/download/latest/enola.x86_64.tgz

/tmp/enola/distro/jpackage/out/enola/bin/enola --version

# TODO https://github.com/enola-dev/enola/issues/1158 Mac & Windows?
