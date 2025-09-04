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
    name = "io",
    srcs = glob(
        [
            "*.java",
            # TODO Break up into separate modules
            "mediatype/*.java",
            "hashbrown/*.java",
            "resource/**/*.java",
        ],
        exclude = ["**/*Test.java"],
    ),
    plugins = ["//tools/bazel/java_plugin:autoservice"],
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/common",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/data",
        # TODO Separate BUILD for IPFSResource
        "@maven//:com_github_ipld_java_cid",
        "@maven//:com_github_multiformats_java_multihash",
        "@maven//:com_github_multiformats_java_multibase",
        "@maven//:com_google_auto_service_auto_service_annotations",
        "@maven//:com_google_errorprone_error_prone_annotations",
        "@maven//:com_google_guava_guava",
        # TODO Separate BUILD for OkHttpResource
        "@maven//:com_squareup_okhttp3_logging_interceptor",
        "@maven//:com_squareup_okhttp3_okhttp",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob([
        "mediatype/*Test.java",
        "resource/**/*Test.java",
        "hashbrown/**/*Test.java",
    ]),
    plugins = ["//tools/bazel/java_plugin:autoservice"],
    deps = [
        ":io",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/protobuf",
        "//test",
        "@maven//:com_github_ipld_java_cid",
        "@maven//:com_github_java_json_tools_uri_template",
        "@maven//:com_github_multiformats_java_multibase",
        "@maven//:com_github_multiformats_java_multihash",
        "@maven//:com_google_auto_service_auto_service_annotations",
        "@maven//:org_jspecify_jspecify",
    ],
)
