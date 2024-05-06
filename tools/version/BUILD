load("@rules_java//java:defs.bzl", "java_library")

# This at first seemed to sort of semi-work, only;
# but actually doesn't work at all - why does it fail:
# "declared output 'tools/version/VERSION' was not created by genrule"
#
# We need local = True because version.bash needs to run on Git tree.
# But that (local) misses manually removed `outs` files like VERSION
# and does not rebuild in that case, which is confusing.

# genrule(
#    name = "gen-VERSION",
#    srcs = ["version.bash"],
#    outs = ["VERSION"],
#    cmd_bash = "$(location version.bash)",
#    local = True,
#    tools = ["version.bash"],
# )

java_library(
    name = "version",
    resource_strip_prefix = "tools/version",
    # buildifier: disable=constant-glob
    resources = glob(
        ["VERSION"],
        allow_empty = True,
    ),
    visibility = ["//:__subpackages__"],
    # TODO runtime_deps = [":gen-VERSION"],
)
