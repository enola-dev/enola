load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "models",
    resource_strip_prefix = "models",
    resources = glob(
        ["**/*"],
        exclude = [
            "BUILD",
            "build.bash",
            "example.org/**",
        ],
    ) + ["example.org/greetingN.ttl"],
    visibility = ["//:__subpackages__"],
)
