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

load("@rules_java//java:defs.bzl", "java_binary", "java_library", "java_test")
load("//tools/bazel:junit.bzl", "junit_tests")

java_binary(
    name = "enola",
    main_class = "dev.enola.cli.EnolaApplication",
    runtime_deps = [":lib"],
)

java_library(
    # TODO Rename to cli
    name = "lib",
    srcs = glob(
        ["*.java"],
        exclude = [
            "*Test.java",
            "CommandLineSubject.java",
            "SystemOutErrCapture.java",
        ],
    ),
    plugins = ["//tools/bazel/java_plugin:picocli"],
    resource_strip_prefix = "java/dev/enola/cli/resources",
    resources = glob(["resources/**"]),
    # TODO Change "//java/dev/enola:__pkg__" visibility to tools/javadoc/all
    visibility = ["//java/dev/enola:__pkg__"],
    deps = [
        "//generated/protoc/java",
        "//java/dev/enola/ai/adk/core",
        # TODO Remove most of these direct deps, through new "//java/dev/enola" API...
        "//java/dev/enola/ai/adk/iri",
        "//java/dev/enola/ai/adk/tool",
        "//java/dev/enola/ai/adk/web",
        "//java/dev/enola/ai/dotagent",
        "//java/dev/enola/ai/mcp",
        "//java/dev/enola/ai/mcp/cli",
        "//java/dev/enola/cas",
        "//java/dev/enola/chat",
        "//java/dev/enola/chat/sshd",
        "//java/dev/enola/cli/common",
        "//java/dev/enola/common",
        "//java/dev/enola/common/canonicalize",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/linereader",
        "//java/dev/enola/common/linereader/jline",
        "//java/dev/enola/common/locale",
        "//java/dev/enola/common/markdown/exec:markdown",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/secret",
        "//java/dev/enola/common/secret/auto",
        "//java/dev/enola/common/xml",
        "//java/dev/enola/data/iri",
        "//java/dev/enola/data/iri/namespace/repo",
        "//java/dev/enola/identity",
        "//java/dev/enola/infer/datalog",
        "//java/dev/enola/infer/rdf",
        "//java/dev/enola/core",
        "//java/dev/enola/data",
        "//java/dev/enola/datatype",
        "//java/dev/enola/format/tika",
        "//java/dev/enola/format/xml",
        "//java/dev/enola/model",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/rdf/proto",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing/gen",
        "//java/dev/enola/web",
        "//java/dev/enola/web/netty",
        "@maven//:com_google_genai_google_genai",
        "@maven//:com_github_ipld_java_cid",
        "@maven//:com_github_multiformats_java_multibase",
        "@maven//:com_github_multiformats_java_multihash",
        # "@maven//:com_google_adk_google_adk",
        "@maven//:com_github_vorburger_adk_java_google_adk",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:info_picocli_picocli",
        "@maven//:info_picocli_picocli_shell_jline3",
        # TODO "@maven//:org_apache_httpcomponents_client5_httpclient5",
        "@maven//:io_reactivex_rxjava3_rxjava",
        "@maven//:org_jline_jline",
        "@maven//:org_eclipse_rdf4j_rdf4j_model",
        "@maven//:org_eclipse_rdf4j_rdf4j_rio_api",
        "@maven//:org_jspecify_jspecify",
        "@maven//:org_slf4j_jcl_over_slf4j",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

java_library(
    name = "testlib",
    srcs = [
        "CommandLineSubject.java",
        "SystemOutErrCapture.java",
    ],
    deps = [
        "//java/dev/enola/cli/common",
        "@maven//:com_google_truth_truth",
    ],
)

junit_tests(
    name = "tests",
    size = "medium",
    srcs = glob(
        ["*Test.java"],
        exclude = ["EnolaLoggingTest.java"],
    ),
    deps = [
        ":lib",
        ":testlib",
        "//java/dev/enola/cli/common",
        "//java/dev/enola/common/canonicalize",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/markdown/exec:markdown",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/infer/datalog",
        "//java/dev/enola/infer/rdf",
        "//java/dev/enola/model",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing/gen",
        "//java/dev/enola/web",
        "//java/dev/enola/web/netty",
        "//models",
        "//test",
        "@maven//:info_picocli_picocli",
        "@maven//:io_github_classgraph_classgraph",
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
)

java_test(
    name = "EnolaLoggingTest",
    srcs = [
        "EnolaLoggingTest.java",
    ],
    runtime_deps = [
        "//java/dev/enola/common/canonicalize",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/markdown/exec:markdown",
        "//java/dev/enola/core",
        "//java/dev/enola/infer/datalog",
        "//java/dev/enola/web",
        "@maven//:org_apache_logging_log4j_log4j_to_jul",
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
    deps = [
        ":lib",
        ":testlib",
        "//java/dev/enola/cli/common",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/thing:thing_java",
        "@maven//:com_google_truth_truth",
        "@maven//:info_picocli_picocli",
        "@maven//:junit_junit",
    ],
)
