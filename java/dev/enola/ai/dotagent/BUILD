# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2025 The Enola <https://enola.dev> Authors
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
load("@rules_java//java:java_binary.bzl", "java_binary")
load("//tools/bazel:junit.bzl", "junit_tests")

java_binary(
    name = "demo",
    main_class = "dev.enola.ai.agent.Demo",
    runtime_deps = [
        ":dotagent",
        "@enola_maven//:org_slf4j_slf4j_simple",
    ],
)

java_library(
    name = "dotagent",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
        ],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/ai/adk/core",
        "//java/dev/enola/ai/adk/iri",
        "//java/dev/enola/ai/adk/test",
        "//java/dev/enola/ai/dotprompt",
        "//java/dev/enola/ai/iri",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/secret",
        "@enola_maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@enola_maven//:com_google_adk_google_adk",
        "@enola_maven//:com_google_genai_google_genai",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:io_reactivex_rxjava3_rxjava",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(
        ["*Test.java"],
    ),
    deps = [
        ":dotagent",
        "//java/dev/enola/ai/adk/iri",
        "//java/dev/enola/ai/adk/test",
        "//java/dev/enola/common/io",
    ],
)
