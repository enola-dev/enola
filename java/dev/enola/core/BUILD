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
load("//tools/bazel:junit.bzl", "junit_tests")

java_library(
    name = "core",
    srcs = glob(
        ["**/*.java"],
        exclude = [
            "**/*Test.java",
        ],
    ),
    visibility = ["//:__subpackages__"],
    runtime_deps = [
        "@maven//:io_grpc_grpc_netty",
    ],
    deps = [
        "//generated/protoc/java",
        "//java/dev/enola/common",
        "//java/dev/enola/common/concurrent",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/yamljson",
        "//java/dev/enola/data",
        "//java/dev/enola/data/iri",
        "//java/dev/enola/datatype",
        "//java/dev/enola/data/iri/template",
        # TODO Remove tika dependency from core once Rosetta is ServiceLoader-based
        "//java/dev/enola/format/tika/rdf",
        "//java/dev/enola/format/xml",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing/gen",
        "//models",
        "@maven//:com_github_java_json_tools_uri_template",
        "@maven//:com_google_errorprone_error_prone_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_inprocess",
        "@maven//:io_grpc_grpc_stub",
        "@maven//:io_grpc_grpc_protobuf_lite",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(["**/*Test.java"]),
    runtime_deps = [
        "@maven//:io_grpc_grpc_netty",
        "@maven//:org_slf4j_slf4j_simple",
    ],
    deps = [
        "//generated/protoc/java",
        "//java/dev/enola/common/concurrent",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/io/testlib",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/xml",
        "//java/dev/enola/common/yamljson",  # TODO Remove this once dependencies are cleaned up
        "//java/dev/enola/core",
        "//java/dev/enola/data",
        "//java/dev/enola/data/iri",
        "//java/dev/enola/data/iri/namespace/repo",
        "//java/dev/enola/datatype",
        "//java/dev/enola/model",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing/gen",
        "//test",
    ],
)
