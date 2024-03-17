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

load("@rules_java//java:defs.bzl", "java_library", "java_test")

java_library(
    name = "impl",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    visibility = [
        "//cli:__subpackages__",
        "//connectors:__subpackages__",  #  ONLY for test, NEVER for non-test Demo (or any other) Connector binary!
        # TODO Move package dev.enola.core.grpc from impl to lib_java, and remove this:
        "//web/rest:__pkg__",
        "//web/ui:__pkg__",
    ],
    runtime_deps = [
        "@maven//:io_grpc_grpc_netty",
    ],
    deps = [
        "//common/common",
        "//common/protobuf",
        "//common/protobuf:validation_proto_java_library",
        "//common/rdf",
        "//common/thing:thing_java",
        "//common/thing:thing_java_proto",
        "//common/yamljson",
        "//core/lib:connector_java_grpc",
        "//core/lib:core_java_grpc",
        "//core/lib:core_java_proto",
        "//core/lib:lib_java",
        "@maven//:com_github_java_json_tools_uri_template",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_inprocess",
        "@maven//:io_grpc_grpc_stub",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

[java_test(
    name = name[:-len(".java")],
    size = "small",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**/*"]),
    runtime_deps = [
        "@maven//:io_grpc_grpc_netty",
        "@maven//:org_slf4j_slf4j_simple",
    ],
    deps = [
        ":impl",
        "//common/common",
        "//common/protobuf",
        "//common/thing:thing_java_proto",
        "//common/yamljson",  # TODO Remove this once dependencies are cleaned up
        "//connectors/demo",
        "//connectors/demo:demo_java_proto",
        "//core/lib:connector_java_grpc",
        "//core/lib:core_java_grpc",
        "//core/lib:core_java_proto",
        "//core/lib:lib_java",
        "@maven//:com_github_java_json_tools_uri_template",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_truth_extensions_truth_java8_extension",
        "@maven//:com_google_truth_truth",
        "@maven//:io_grpc_grpc_api",
        "@maven//:junit_junit",
    ],
) for name in glob([
    "src/test/java/**/*Test.java",
])]
