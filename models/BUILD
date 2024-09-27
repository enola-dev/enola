load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "models",
    resource_strip_prefix = "models",
    resources = glob(
        ["**/*"],
        exclude = [
            "BUILD",
            "build.bash",
        ],
    ),
    visibility = ["//:__subpackages__"],
)
