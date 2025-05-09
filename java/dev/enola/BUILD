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
    runtime_deps = [
        "//java/dev/enola/common",
        "//java/dev/enola/common/canonicalize",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/markdown/exec:markdown",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/xml",
        "//java/dev/enola/core",
        "//java/dev/enola/core:core_java_grpc",
        "//java/dev/enola/core:core_java_proto",
        "//java/dev/enola/data",
        "//java/dev/enola/datatype",
        "//java/dev/enola/format/tika",
        "//java/dev/enola/format/xml",
        "//java/dev/enola/infer/datalog",
        "//java/dev/enola/model",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/rdf/proto",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing:thing_java_proto",
        "//java/dev/enola/thing/gen",
        "//java/dev/enola/web",
        "//java/dev/enola/web/netty",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:com_google_protobuf_protobuf_java",
        "@enola_maven//:org_apache_logging_log4j_log4j_to_jul",
        "@enola_maven//:org_eclipse_rdf4j_rdf4j_model",
        "@enola_maven//:org_eclipse_rdf4j_rdf4j_rio_api",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
        "@enola_maven//:org_slf4j_slf4j_jdk14",
    ],
)

# This is used by tools/docs/build.bash (and tools/maven/install.bash)
javadoc(
    name = "javadoc",
    doc_url = "https://docs.enola.dev/dev/javadoc/",
    deps = [
        ":enola",
        # TODO Remove, but how not to fail?!
        "@enola_maven//:com_google_protobuf_protobuf_java",
        "@enola_maven//:com_google_protobuf_protobuf_java_util",
        "@enola_maven//:javax_annotation_javax_annotation_api",
        # NB: We cannot use "@enola_maven//:com_google_guava_guava" here,
        # due to https://github.com/bazel-contrib/rules_jvm_external/issues/1343.
    ],
)
