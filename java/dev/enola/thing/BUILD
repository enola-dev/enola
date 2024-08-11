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

load("@rules_java//java:defs.bzl", "java_library", "java_proto_library")
load("@rules_proto//proto:defs.bzl", "proto_library")
load("@rules_proto_grpc//buf:defs.bzl", "buf_proto_lint_test")
load("@rules_proto_grpc//doc:defs.bzl", "doc_markdown_compile")
load("//tools/bazel:junit.bzl", "junit_tests")

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

doc_markdown_compile(
    name = "thing_proto_doc",
    protos = [
        "thing_proto",
    ],
)

java_proto_library(
    name = "thing_java_proto",
    visibility = ["//:__subpackages__"],
    deps = ["thing_proto"],
)

java_library(
    name = "thing_java",
    srcs = glob(
        # TODO Separate BUILD for each sub-package
        ["**/*.java"],
        exclude = [
            "**/*Test*.java",
            "**/Test*.java",
        ],
    ),
    plugins = ["//tools/bazel/java_plugin:autoservice"],
    visibility = ["//:__subpackages__"],
    deps = [
        ":thing_java_proto",
        "//java/dev/enola/common",
        "//java/dev/enola/common/collect",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/time",
        "//java/dev/enola/common/tree",
        "//java/dev/enola/data",
        "//java/dev/enola/datatype",
        "@maven//:com_github_java_json_tools_uri_template",
        "@maven//:com_github_spotbugs_spotbugs_annotations",
        "@maven//:com_google_auto_service_auto_service_annotations",
        "@maven//:com_google_errorprone_error_prone_annotations",
        "@maven//:com_google_errorprone_error_prone_type_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(["**/*Test.java"]),
    srcs_utils = glob(
        ["**/*Test*.java"],
        exclude = ["**/*Test.java"],
    ),
    deps = [
        ":thing_java",
        ":thing_java_proto",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/protobuf:test_java_proto",
        "//java/dev/enola/data",
        "//java/dev/enola/datatype",
        "//java/dev/enola/model",
        "//java/dev/enola/rdf",
        "//models",
        "//test",
        "@maven//:com_github_spotbugs_spotbugs_annotations",
        "@maven//:com_google_errorprone_error_prone_annotations",
        "@maven//:org_jspecify_jspecify",
    ],
)
