load("@rules_java//java:defs.bzl", "java_binary", "java_library")

java_binary(
    name = "demo",
    main_class = "dev.enola.common.logging.LoggingDemo",
    runtime_deps = [":logging"],
)

java_library(
    name = "logging",
    srcs = glob(["*.java"]),
    visibility = ["//visibility:public"],
    runtime_deps = [
        "@maven//:org_apache_logging_log4j_log4j_to_jul",
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
    deps = [
        "@maven//:org_slf4j_jcl_over_slf4j",
        "@maven//:org_slf4j_slf4j_api",
    ],
)
