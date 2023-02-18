# Bazel

## Update Existing Maven dependencies

As per https://github.com/bazelbuild/rules_jvm_external/blob/master/README.md#outdated-artifacts :

1. `b run @maven//:outdated`

## Add New Maven dependencies

As per https://github.com/bazelbuild/rules_jvm_external/blob/master/README.md#updating-maven_installjson :

1. Edit `MODULE.bazel`

1. `b run @unpinned_maven//:pin`

PS: Use `query "@maven//:*"` to see all targets.
