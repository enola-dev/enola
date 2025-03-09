load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "test",
    resource_strip_prefix = "test",
    resources = glob(
        ["**/*"],
        exclude = ["BUILD"],
    ),
    visibility = ["//:__subpackages__"],
)
