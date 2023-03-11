<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023 The Enola <https://enola.dev> Authors

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

## Maven dependencies

### Update Existing Maven dependencies

As per https://github.com/bazelbuild/rules_jvm_external/blob/master/README.md#outdated-artifacts :

1. `b run @maven//:outdated`

### Add New Maven dependencies

As per https://github.com/bazelbuild/rules_jvm_external/blob/master/README.md#updating-maven_installjson :

1. Edit `MODULE.bazel`

1. `b run @unpinned_maven//:pin`

PS: Use `b query "@maven//:*"` to see all targets.

## GitHub Action Cache

The `.github/workflows/test.yaml` uses the https://github.com/actions/cache to speed up the Continuous Integration (CI) builds of PRs on GitHub.

It is **normal** and intentional that every time Bazel `BUILD` etc. files are changed this cache is _entirely_ invalidated, and thus a longer "full build" runs. PRs which do not change dependencies will still build noticeably faster.

If you are surprised and it seems to you that this cache does not work, because you noticed that the
re-build of the `main` branch after the merge of a GitHub Pull Request (PR) which changed dependencies
does not re-use the cache from the PR, this is also **expected** and not a bug.

It **WILL** work and speed up future PRs (provided there are no changes to dependencies again).

[GitHub's documentation](https://docs.github.com/en/actions/using-workflows/caching-dependencies-to-speed-up-workflows#restrictions-for-accessing-a-cache)
explains why this is so; TL;DR: a PR build can use the cache from a `main` build, but a `main` build will not use a PR's cache.

https://github.com/actions/cache/pull/575/files has some related discussion.
