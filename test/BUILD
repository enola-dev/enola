load("@rules_java//java:defs.bzl", "java_library")

# This is actually a pretty Bad Idea from a build efficiency PoV
# (because every change in //test causes rebuilds of everything...)
# but it's "convenient" - and the documentation links to this, so
# breaking it up to nicely modularize test data is a bit of work.

java_library(
    name = "test",
    resource_strip_prefix = "test",
    resources = glob(
        ["**/*"],
        exclude = ["BUILD"],
    ),
    visibility = ["//:__subpackages__"],
)
