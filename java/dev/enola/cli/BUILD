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

load("@rules_java//java:defs.bzl", "java_binary", "java_test")
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
    main_class = "dev.enola.cli.EnolaCLI",
    deps = [
        "@enola_maven//:com_github_multiformats_java_multibase",
        "@enola_maven//:com_github_multiformats_java_multihash",
        "//java/dev/enola",
        # TODO Remove most of these direct deps, through new Enola API...
        "//java/dev/enola/cas",
        "//java/dev/enola/common",
        "//java/dev/enola/common/canonicalize",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/markdown/exec:markdown",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/xml",
        "//java/dev/enola/infer/datalog",
        "//java/dev/enola/core",
        "//java/dev/enola/core:core_java_grpc",
        "//java/dev/enola/core:core_java_proto",
        "//java/dev/enola/data",
        "//java/dev/enola/datatype",
        "//java/dev/enola/format/tika",
        "//java/dev/enola/format/xml",
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
        "@enola_maven//:info_picocli_picocli",
        "@enola_maven//:org_eclipse_rdf4j_rdf4j_model",
        "@enola_maven//:org_eclipse_rdf4j_rdf4j_rio_api",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    size = "medium",
    srcs = glob(
        ["*Test.java"],
        exclude = ["EnolaLoggingTest.java"],
    ),
    srcs_utils = [
        "CommandLineSubject.java",
        "SystemOutErrCapture.java",
    ],
    deps = [
        ":enola",
        "//java/dev/enola/common/canonicalize",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/markdown/exec:markdown",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/infer/datalog",
        "//java/dev/enola/model",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing/gen",
        "//java/dev/enola/web",
        "//java/dev/enola/web/netty",
        "//models",
        "//test",
        "@enola_maven//:info_picocli_picocli",
        "@enola_maven//:io_github_classgraph_classgraph",
        "@enola_maven//:org_slf4j_slf4j_jdk14",
    ],
)

java_test(
    name = "EnolaLoggingTest",
    srcs = [
        "CommandLineSubject.java",
        "EnolaLoggingTest.java",
        "SystemOutErrCapture.java",
    ],
    runtime_deps = [
        "//java/dev/enola/common/canonicalize",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/markdown/exec:markdown",
        "//java/dev/enola/core",
        "//java/dev/enola/infer/datalog",
        "//java/dev/enola/web",
        "@enola_maven//:org_apache_logging_log4j_log4j_to_jul",
        "@enola_maven//:org_slf4j_slf4j_jdk14",
    ],
    deps = [
        ":enola",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/thing:thing_java",
        "@enola_maven//:com_google_truth_truth",
        "@enola_maven//:info_picocli_picocli",
        "@enola_maven//:junit_junit",
    ],
)
