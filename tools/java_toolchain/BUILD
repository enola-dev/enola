# https://github.com/enola-dev/enola/issues/1806

load(
    "@bazel_tools//tools/jdk:default_java_toolchain.bzl",
    # See https://github.com/bazelbuild/rules_java/issues/325:
    "NONPREBUILT_TOOLCHAIN_CONFIGURATION",
    "default_java_toolchain",
)
#load("@rules_java//java/toolchains:java_runtime.bzl", "java_runtime")

#java_runtime(
#    name = "jdk_runtime",
#    # TODO Replace hard-coded /nix/store path with JAVA_HOME; using 'common --action_env=JAVA_HOME' in .bazelrc?
#    java_home = "/nix/store/hm028d05qs6138ggq02239sh6qf5xwd6-openjdk-21.0.8+9/lib/openjdk",
#    visibility = ["//visibility:public"],
#)

default_java_toolchain(
    name = "repository_default_java_toolchain",
    # See https://github.com/bazelbuild/rules_java/issues/325:
    configuration = NONPREBUILT_TOOLCHAIN_CONFIGURATION,

    # INSTEAD of: java_runtime = "@rules_java//toolchains:remotejdk_21",
    # OR above's: java_runtime = ":jdk_runtime",
    java_runtime = "@bazel_tools//tools/jdk:current_host_java_runtime",

    # Do *NOT* add "--enable-preview" javacopts + jvm_opts here!!
    # (See https://github.com/enola-dev/enola/issues/511 for why not.)
    source_version = "21",
    target_version = "21",
)
