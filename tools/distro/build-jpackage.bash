#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2025 The Enola <https://enola.dev> Authors
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
  --module-path /usr/lib/jvm/java-21-openjdk/jmods --add-modules jdk.management.jfr,java.rmi,jdk.jdi,jdk.charsets,java.xml,jdk.xml.dom,java.datatransfer,jdk.jstatd,jdk.httpserver,java.desktop,java.security.sasl,jdk.zipfs,java.base,jdk.crypto.ec,jdk.javadoc,jdk.management.agent,jdk.jshell,jdk.editpad,jdk.sctp,java.sql.rowset,jdk.jsobject,jdk.unsupported,java.smartcardio,jdk.jlink,java.security.jgss,java.compiler,jdk.nio.mapmode,jdk.dynalink,jdk.unsupported.desktop,jdk.accessibility,jdk.security.jgss,java.sql,jdk.incubator.vector,java.xml.crypto,java.logging,java.transaction.xa,jdk.jfr,jdk.crypto.cryptoki,jdk.net,jdk.random,java.naming,jdk.internal.ed,java.prefs,java.net.http,jdk.compiler,jdk.internal.opt,jdk.naming.rmi,jdk.jconsole,jdk.attach,jdk.internal.le,java.management,jdk.jdwp.agent,jdk.internal.jvmstat,java.instrument,jdk.management,jdk.security.auth,java.scripting,jdk.jdeps,jdk.jartool,java.management.rmi,jdk.jpackage,jdk.naming.dns,jdk.localedata \
  --strip-native-commands --strip-debug --no-man-pages --no-header-files

jpackage --verbose --type app-image \
  --runtime-image /tmp/enola/distro/jpackage/out/jlink \
  --input /tmp/enola/distro/jpackage/in \
  --main-class dev.enola.cli.EnolaApplication --main-jar enola_deploy.jar \
  --app-version 0.0.1 --copyright "Copyright 2023-2025 The Enola Authors" \
  --description "See https://docs.enola.dev" \
  --dest /tmp/enola/distro/jpackage/out -n enola
tar czf site/download/latest/enola.tgz -C /tmp/enola/distro/jpackage/out/ enola
ls -lh site/download/latest/enola.tgz

/tmp/enola/distro/jpackage/out/enola/bin/enola --version

# --type rpm --about-url https://docs.enola.dev \
#  mv -v "$ROOT"/enola-*.rpm site/download/latest/enola.x86_64.rpm
#  git restore --staged enola-*.rpm

# TODO Customize jlink for smaller JRE with less modules

# Nota bene: jpackage needs to run on the platform for which it generates a package
# TODO Explore https://jreleaser.org to see if it has cross-platform (Mac) GitHub Actions?
