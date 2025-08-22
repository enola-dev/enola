# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023 The Enola <https://enola.dev> Authors
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

load("@rules_java//java:defs.bzl", "java_library")
load("@rules_jvm_external//:defs.bzl", "java_export", "javadoc")

java_library(
    name = "api",
    srcs = glob(
        ["*.java"],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/thing:thing_java",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:org_jspecify_jspecify",
    ],
)

java_export(
    name = "enola",
    maven_coordinates = "dev.enola:enola:0.0.1-SNAPSHOT",
    # NB: JavaDoc does not work here, but below it's fine
    tags = ["no-javadocs"],
    visibility = ["//:__subpackages__"],
    # TODO runtime_deps or exports = [ ?!
    runtime_deps = ["//java/dev/enola/cli:lib"],
)

# TODO Replace Bazel JavaDoc with tools/javadoc/build.bash (but Maven JAR?)
# This is used by tools/docs/build.bash (and tools/maven/install.bash)
javadoc(
    name = "javadoc",
    doc_url = "https://docs.enola.dev/dev/javadoc/",
    # Note that even merely javadocopts = [] makes the build fail?! ;=(
    # TODO javadocopts = [
    #   TODO "-Xdoclint:all",
    #   TODO "-Werror",
    #"-encoding",
    #"UTF-8",
    #"-charset",
    #"UTF-8",
    #],
    deps = [
        ":enola",
        # TODO Remove, but how not to fail?! https://github.com/bazel-contrib/rules_jvm_external/issues/1344
        "@enola_maven//:com_google_protobuf_protobuf_java",
        "@enola_maven//:com_google_protobuf_protobuf_java_util",
        "@enola_maven//:javax_annotation_javax_annotation_api",
        # NB: We cannot use "@enola_maven//:com_google_guava_guava" here,
        # due to https://github.com/bazel-contrib/rules_jvm_external/issues/1343.
    ],
)
