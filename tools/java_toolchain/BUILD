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

# https://github.com/bazelbuild/bazel/issues/21119
# https://bazel.build/docs/bazel-and-java#config-compilation-toolchains

# NB: This is "activated" by register_toolchains() in WORKSPACE.bazel!

load(
    "@bazel_tools//tools/jdk:default_java_toolchain.bzl",
    "BASE_JDK9_JVM_OPTS",
    "DEFAULT_JAVACOPTS",
    "DEFAULT_TOOLCHAIN_CONFIGURATION",
    "default_java_toolchain",
)

default_java_toolchain(
    name = "repository_default_java_toolchain",
    configuration = DEFAULT_TOOLCHAIN_CONFIGURATION,  # One of predefined configurations
    # Other parameters are from java_toolchain rule:
    java_runtime = "@rules_java//toolchains:remotejdk_21",  # JDK to use for compilation and toolchain's tools execution
    javacopts = DEFAULT_JAVACOPTS + ["--enable_preview"],  # Additional javac options
    jvm_opts = BASE_JDK9_JVM_OPTS + ["--enable_preview"],  # Additional JDK options
    source_version = "21",
    target_version = "21",
)
