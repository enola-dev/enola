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
    name = "proto",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
            "*Subject.java",
        ],
    ),
    plugins = ["//tools/bazel/java_plugin:autoservice"],
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/yamljson",
        "//java/dev/enola/data/iri/namespace/repo",
        "//java/dev/enola/datatype",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing:thing_java_proto",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:com_google_auto_service_auto_service_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_protobuf_protobuf_java_util",
        "@maven//:no_hasmac_hasmac_json_ld",
        "@maven//:org_eclipse_rdf4j_rdf4j_common_io",
        "@maven//:org_eclipse_rdf4j_rdf4j_model",
        "@maven//:org_eclipse_rdf4j_rdf4j_model_api",
        "@maven//:org_eclipse_rdf4j_rdf4j_model_vocabulary",
        "@maven//:org_eclipse_rdf4j_rdf4j_rio_api",
        "@maven//:org_eclipse_rdf4j_rdf4j_rio_jsonld",
        "@maven//:org_eclipse_rdf4j_rdf4j_rio_turtle",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(
        include = ["*Test.java"],
        allow_empty = True,
    ),
    deps = [
        ":proto",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/testlib",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/protobuf:test_java_proto",
        "//java/dev/enola/common/yamljson",
        "//java/dev/enola/datatype",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing:thing_java_proto",
        "//test",
        "@maven//:org_eclipse_rdf4j_rdf4j_model",
        "@maven//:org_eclipse_rdf4j_rdf4j_model_api",
        "@maven//:org_eclipse_rdf4j_rdf4j_model_vocabulary",
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
)
