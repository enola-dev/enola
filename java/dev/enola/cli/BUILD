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

load("@rules_java//java:defs.bzl", "java_binary")
load("//tools/bazel:junit.bzl", "junit_tests")

java_binary(
    name = "enola",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
            "CommandLineSubject.java",
            "SystemOutErrCapture.java",
        ],
    ),
    jvm_flags = ["--enable-preview"],
    main_class = "dev.enola.cli.EnolaCLI",
    deps = [
        "//java/dev/enola/common",
        "//java/dev/enola/common/canonicalize",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/markdown/exec:markdown",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/core",
        "//java/dev/enola/core:core_java_grpc",
        "//java/dev/enola/core:core_java_proto",
        "//java/dev/enola/data",
        "//java/dev/enola/datatype",
        "//java/dev/enola/format/tika",
        "//java/dev/enola/format/xml",
        "//java/dev/enola/model",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing:thing_java_proto",
        "//java/dev/enola/thing/gen",
        "//java/dev/enola/web",
        "//java/dev/enola/web/netty",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:info_picocli_picocli",
        "@maven//:org_apache_logging_log4j_log4j_to_jul",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
)

junit_tests(
    name = "tests",
    size = "medium",
    srcs = glob(
        ["*Test.java"],
    ),
    jvm_flags = ["--enable-preview"],
    srcs_utils = [
        "CommandLineSubject.java",
        "SystemOutErrCapture.java",
    ],
    deps = [
        ":enola",
        "//java/dev/enola/common/canonicalize",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/markdown/exec:markdown",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/model",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing/gen",
        "//java/dev/enola/web",
        "//java/dev/enola/web/netty",
        "//models",
        "//test",
        "@maven//:info_picocli_picocli",
        "@maven//:io_github_classgraph_classgraph",
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
)
