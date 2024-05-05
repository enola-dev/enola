load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "version",
    resource_strip_prefix = "tools/version",
    # buildifier: disable=constant-glob
    resources = glob(
        ["VERSION"],
        allow_empty = True,
    ),
    visibility = ["//:__subpackages__"],
)
