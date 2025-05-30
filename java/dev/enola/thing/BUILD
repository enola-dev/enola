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
load("@rules_go//proto:def.bzl", "go_proto_library")
load("@rules_java//java:defs.bzl", "java_library")
load("//tools/bazel:junit.bzl", "junit_tests")

proto_library(
    name = "thing_proto",
    srcs = ["thing.proto"],
    visibility = ["//:__subpackages__"],
    deps = [
        "@protobuf//:timestamp_proto",
    ],
)

java_proto_library(
    name = "thing_java_proto",
    visibility = ["//:__subpackages__"],
    deps = ["thing_proto"],
)

go_proto_library(
    name = "thing_go_proto",
    importpath = "dev/enola/thing",
    protos = [":thing_proto"],
    visibility = ["//:__subpackages__"],
    deps = [],
)

java_library(
    # TODO Rename to thing
    name = "thing_java",
    srcs = glob(
        # TODO Separate BUILD for each sub-package
        ["**/*.java"],
        exclude = [
            "**/*Test.java",
            "**/ThingTester.java",
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
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/io/metadata",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/string2long",
        "//java/dev/enola/common/time",
        "//java/dev/enola/common/tree",
        "//java/dev/enola/data",
        "//java/dev/enola/data/iri",
        "//java/dev/enola/data/iri/namespace/repo",
        "//java/dev/enola/data/iri/template",
        "//java/dev/enola/datatype",
        "@enola_maven//:com_github_java_json_tools_uri_template",
        "@enola_maven//:com_github_spotbugs_spotbugs_annotations",
        "@enola_maven//:com_google_auto_service_auto_service_annotations",
        "@enola_maven//:com_google_errorprone_error_prone_annotations",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:com_google_protobuf_protobuf_java",
        "@enola_maven//:com_google_protobuf_protobuf_java_util",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(["**/*Test.java"]),
    srcs_utils = glob(
        ["**/ThingTester.java"],
        exclude = ["**/*Test.java"],
    ),
    deps = [
        ":thing_java",
        ":thing_java_proto",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/protobuf:test_java_proto",
        "//java/dev/enola/data",
        "//java/dev/enola/data/iri",
        "//java/dev/enola/data/iri/namespace/repo",
        "//java/dev/enola/datatype",
        "//java/dev/enola/model",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/rdf/proto",
        "//models",
        "//test",
        "@enola_maven//:com_github_spotbugs_spotbugs_annotations",
        "@enola_maven//:com_google_errorprone_error_prone_annotations",
        "@enola_maven//:org_eclipse_rdf4j_rdf4j_model_api",
        "@enola_maven//:org_eclipse_rdf4j_rdf4j_model_vocabulary",
        "@enola_maven//:org_jspecify_jspecify",
    ],
)
