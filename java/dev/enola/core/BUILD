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

# https://github.com/enola-dev/enola/issues/202
# https://github.com/grpc/grpc-java/issues/10215

load("@io_grpc_grpc_java//:java_grpc_library.bzl", "java_grpc_library")
load("//tools/bazel:junit.bzl", "junit_tests")

# https://github.com/bazelbuild/rules_go/blob/master/proto/core.rst#go_proto_library

load("@protobuf//bazel:java_proto_library.bzl", "java_proto_library")
load("@protobuf//bazel:proto_library.bzl", "proto_library")
load("@rules_go//proto:def.bzl", "go_proto_library")
load("@rules_java//java:defs.bzl", "java_library")

proto_library(
    name = "ext_proto",
    srcs = ["enola_ext.proto"],
    deps = [
        "@protobuf//:descriptor_proto",
    ],
)

proto_library(
    name = "core_proto",
    srcs = ["enola_core.proto"],
    deps = [
        ":ext_proto",
        "//java/dev/enola/thing:thing_proto",
        "@protobuf//:any_proto",
        "@protobuf//:descriptor_proto",
        "@protobuf//:struct_proto",
    ],
)

proto_library(
    name = "util_proto",
    srcs = ["util/enola_util.proto"],
    deps = [
        "@protobuf//:timestamp_proto",
    ],
)

java_proto_library(
    name = "core_java_proto",
    visibility = ["//:__subpackages__"],
    deps = [
        "core_proto",
        "ext_proto",
        "util_proto",
    ],
)

java_grpc_library(
    name = "core_java_grpc",
    srcs = [
        "core_proto",
    ],
    visibility = ["//:__subpackages__"],
    deps = [":core_java_proto"],
)

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
        "//java/dev/enola/common",
        "//java/dev/enola/common/concurrent",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/protobuf:validation_proto_java_library",
        "//java/dev/enola/common/yamljson",
        "//java/dev/enola/core:core_java_grpc",
        "//java/dev/enola/core:core_java_proto",
        "//java/dev/enola/data",
        "//java/dev/enola/data/iri",
        "//java/dev/enola/datatype",
        "//java/dev/enola/data/iri/template",
        # TODO Remove tika dependency from core once Rosetta is ServiceLoader-based
        "//java/dev/enola/format/tika/rdf",
        "//java/dev/enola/format/xml",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing:thing_java_proto",
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
        "//java/dev/enola/core:core_java_grpc",
        "//java/dev/enola/core:core_java_proto",
        "//java/dev/enola/data",
        "//java/dev/enola/data/iri",
        "//java/dev/enola/data/iri/namespace/repo",
        "//java/dev/enola/datatype",
        "//java/dev/enola/model",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing:thing_java_proto",
        "//java/dev/enola/thing/gen",
        "//test",
    ],
)

go_proto_library(
    name = "ext_go_proto",
    importpath = "dev/enola/core/ext",
    protos = [":ext_proto"],
    visibility = [],
)

go_proto_library(
    name = "core_go_proto",
    importpath = "dev/enola/core",
    protos = [":core_proto"],
    visibility = [],
    deps = [
        ":ext_go_proto",
        "//java/dev/enola/thing:thing_go_proto",
    ],
)

go_proto_library(
    name = "util_go_proto",
    importpath = "dev/enola/core/util",
    protos = [":util_proto"],
    visibility = [],
)
