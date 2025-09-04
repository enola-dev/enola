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
load("//tools/bazel:junit.bzl", "junit_tests")

java_library(
    name = "langchain4j",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
        ],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/ai/iri",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/secret",
        "@maven//:com_anthropic_anthropic_java_client_okhttp",
        "@maven//:com_anthropic_anthropic_java_core",
        "@maven//:com_google_guava_guava",
        "@maven//:dev_langchain4j_langchain4j_anthropic",
        "@maven//:dev_langchain4j_langchain4j_core",
        "@maven//:dev_langchain4j_langchain4j_google_ai_gemini",
        "@maven//:dev_langchain4j_langchain4j_ollama",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

junit_tests(
    name = "tests",
    srcs = glob(
        ["*Test.java"],
    ),
    deps = [
        ":langchain4j",
        "//java/dev/enola/ai/iri",
        "//java/dev/enola/common",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/secret",
        "//java/dev/enola/common/secret/auto",
        "//java/dev/enola/data",
        "//java/dev/enola/identity",
        "@maven//:dev_langchain4j_langchain4j_core",
        "@maven//:dev_langchain4j_langchain4j_ollama",
    ],
)
