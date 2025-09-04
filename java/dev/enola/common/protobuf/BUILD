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

load("@protobuf//bazel:java_proto_library.bzl", "java_proto_library")
load("@protobuf//bazel:proto_library.bzl", "proto_library")
load("@rules_java//java:defs.bzl", "java_library")
load("//tools/bazel:junit.bzl", "junit_tests")

proto_library(
    name = "validation_proto",
    srcs = ["validation.proto"],
    deps = [
        "@protobuf//:any_proto",
    ],
)

java_proto_library(
    name = "validation_proto_java_library",
    visibility = ["//:__subpackages__"],
    deps = [
        "validation_proto",
    ],
)

proto_library(
    name = "test_proto",
    srcs = ["test.proto"],
    deps = [
        "@protobuf//:any_proto",
        "@protobuf//:descriptor_proto",
        "@protobuf//:struct_proto",
        "@protobuf//:timestamp_proto",
    ],
)

java_proto_library(
    name = "test_java_proto",
    visibility = ["//:__subpackages__"],
    deps = ["test_proto"],
)

java_library(
    name = "protobuf",
    srcs = glob(
        ["*.java"],
        exclude = ["*Test.java"],
    ),
    plugins = ["//tools/bazel/java_plugin:autoservice"],
    visibility = ["//:__subpackages__"],
    deps = [
        ":validation_proto_java_library",
        "//java/dev/enola/common",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/yamljson",
        "@maven//:com_google_auto_service_auto_service_annotations",
        "@maven//:com_google_errorprone_error_prone_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(["*Test.java"]),
    deps = [
        ":protobuf",
        ":test_java_proto",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/io",
        "//test",
    ],
)
