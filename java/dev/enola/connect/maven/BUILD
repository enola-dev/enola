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
    # TODO Rename to maven
    name = "mima",
    srcs = glob(
        ["*.java"],
        exclude = ["*Test.java"],
    ),
    plugins = ["//tools/bazel/java_plugin:autoservice"],
    visibility = ["//:__subpackages__"],
    deps = [
        "@enola_maven//:com_github_package_url_packageurl_java",
        "@enola_maven//:com_google_auto_service_auto_service_annotations",
        "@enola_maven//:com_google_errorprone_error_prone_annotations",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:eu_maveniverse_maven_mima_context",
        "@enola_maven//:eu_maveniverse_maven_mima_extensions_mmr",
        "@enola_maven//:eu_maveniverse_maven_mima_runtime_standalone_static",
        "@enola_maven//:org_apache_maven_maven_model",
        "@enola_maven//:org_apache_maven_resolver_maven_resolver_api",
        "@enola_maven//:org_apache_maven_resolver_maven_resolver_util",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(["*Test.java"]),
    runtime_deps = [
        # see https://github.com/enola-dev/enola/issues/1116
        "@enola_maven//:org_codehaus_plexus_plexus_xml",
    ],
    deps = [
        ":mima",
        "@enola_maven//:org_apache_maven_resolver_maven_resolver_api",
    ],
)
