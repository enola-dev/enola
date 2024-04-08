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

load("@rules_java//java:defs.bzl", "java_library", "java_proto_library", "java_test")
load("@rules_proto//proto:defs.bzl", "proto_library")
load("@rules_proto_grpc//buf:defs.bzl", "buf_proto_lint_test")

proto_library(
    name = "thing_proto",
    srcs = ["thing.proto"],
    deps = [
        "@com_google_protobuf//:timestamp_proto",
    ],
)

buf_proto_lint_test(
    name = "thing_proto_lint",
    size = "small",
    except_rules = [
        "PACKAGE_VERSION_SUFFIX",
        "PACKAGE_DIRECTORY_MATCH",
    ],
    protos = [
        "thing_proto",
    ],
    use_rules = [
        "DEFAULT",
        "COMMENTS",
    ],
)

java_proto_library(
    name = "thing_java_proto",
    visibility = ["//:__subpackages__"],
    deps = ["thing_proto"],
)

java_library(
    name = "thing_java",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    visibility = ["//:__subpackages__"],
    deps = [
        ":thing_java_proto",
        "//common/common",
        "//common/protobuf",
        "@maven//:com_google_errorprone_error_prone_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

[java_test(
    name = name[:-len(".java")],
    size = "small",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**/*"]),
    runtime_deps = [
        "@maven//:org_slf4j_slf4j_simple",
    ],
    deps = [
        ":thing_java",
        ":thing_java_proto",
        "//common/common",
        "//common/protobuf",
        "//common/protobuf:test_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:com_google_truth_extensions_truth_proto_extension",
        "@maven//:com_google_truth_truth",
        "@maven//:junit_junit",
    ],
) for name in glob([
    "src/test/java/**/*Test.java",
])]
