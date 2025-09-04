<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2025 The Enola <https://enola.dev> Authors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->

# Bazel

## Caching

[Dependencies](dependencies.md) explains how the Bazel cache works on CI.

## Maven dependencies

### Add New Maven dependencies

As per https://github.com/bazelbuild/rules_jvm_external/blob/master/README.md#updating-maven_installjson :

1. Edit [dependencies](dependencies.md) in [`WORKSPACE.bazel`](//WORKSPACE.bazel) (later [`MODULE.bazel`](//MODULE.bazel))

1. `REPIN=1 bazelisk run @maven//:pin`

PS: Use `bazelisk query "@maven//:*"` to see all targets.

### Update Existing Maven dependencies

    ./update.bash

### Dependency Tree

The equivalent of [`mvn dependency:tree`](https://maven.apache.org/plugins/maven-dependency-plugin/tree-mojo.html) is easily visible
in the [`maven_install.json`](//maven_install.json), which lists all `artifacts` versions, transitive `dependencies`, and Java `packages`.
