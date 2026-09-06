# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

"""Starlark rule to generate a JUnit 5 test suite using contrib_rules_jvm."""

load("@contrib_rules_jvm//java:defs.bzl", "java_test_suite")
load("@rules_java//java:defs.bzl", "java_library")

JUNIT5_COMMON_DEPS = [
    "@maven//:com_google_guava_guava",
    "@maven//:com_google_jimfs_jimfs",
    "@maven//:com_google_protobuf_protobuf_java",
    "@maven//:com_google_protobuf_protobuf_java_util",
    "@maven//:com_google_truth_extensions_truth_java8_extension",
    "@maven//:com_google_truth_truth",
    "@maven//:com_google_truth_extensions_truth_proto_extension",
    "@maven//:com_github_valfirst_slf4j_test",
    "@maven//:org_junit_jupiter_junit_jupiter_api",
    "@maven//:org_junit_jupiter_junit_jupiter_params",
    "@maven//:org_junit_jupiter_junit_jupiter_engine",
    "@maven//:org_junit_platform_junit_platform_launcher",
    "@maven//:org_junit_platform_junit_platform_reporting",
    "@maven//:org_opentest4j_opentest4j",
]

def junit_tests(name, srcs, deps, srcs_utils = [], **kwargs):
    """Implementation for JUnit 5 test suites.

    Args:
        name: Rule Name (creates a test_suite with this name)
        srcs: All Tests to run
        deps: Dependencies of Test
        srcs_utils: Utility classes to build but not run tests, if any
        **kwargs: KW Args passed to java_test_suite
    """
    size = kwargs.pop("size", "small")
    timeout = kwargs.pop("timeout", "long")

    seen = {}
    all_deps = []
    for d in list(deps) + JUNIT5_COMMON_DEPS:
        if d not in seen:
            seen[d] = True
            all_deps.append(d)

    if srcs_utils:
        utils_name = name + "_utils"
        java_library(
            name = utils_name,
            testonly = True,
            srcs = srcs_utils,
            deps = all_deps,
        )
        all_deps = all_deps + [":" + utils_name]

    package_prefixes = kwargs.pop("package_prefixes", [".dev."])

    java_test_suite(
        name = name,
        runner = "junit5",
        srcs = srcs,
        deps = all_deps,
        size = size,
        timeout = timeout,
        package_prefixes = package_prefixes,
        **kwargs
    )
