<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2026 The Enola <https://enola.dev> Authors

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

# Dependencies

This project, like any software, stands on the shoulders of giants, and depends on external "third-party" software.

## Updating Dependencies

[`./update.bash`](//update.bash) shows some of and where possible automatically updates these dependencies.
It should be regularly (manually) run by maintainers of this project. Where possible, we automate this with "bots" such as:

* [Bazel 🤵 Steward](https://github.com/VirtusLab/bazel-steward): A bot that automatically updates Bazel dependencies. [Its PRs](https://github.com/enola-dev/enola/commits?author=enola-dev-steward), acting as [`@enola-dev-steward`](https://github.com/enola-dev-steward)
* Renovate: [Dashboard](https://github.com/enola-dev/enola/issues/73), and [its PRs](https://github.com/enola-dev/enola/pulls?q=is%3Apr+author%3Aapp%2Frenovate+)
* Dependabot: [Its PRs](https://github.com/enola-dev/enola/pulls?q=is%3Apr+author%3Aapp%2Fdependabot)

## Runtime Dependencies

* [`WORKSPACE.bazel`](//WORKSPACE.bazel) (later [`MODULE.bazel`](//MODULE.bazel)) contains the Java Maven Group, Artifact & Versions (GAV), see [our Bazel doc](bazel.md)

## Build-time (only) Dependencies

* [`.bazelversion`](//.bazelversion) has the Bazel version itself
* [`WORKSPACE.bazel`](//WORKSPACE.bazel) contains versioned `http_archive` of Bazel rules
* [`MODULE.bazel`](//MODULE.bazel) contains `bazel_dep` versions
* [`.pre-commit-config.yaml`](//.pre-commit-config.yaml) contains the versions of various `pre-commit` hooks used to enforce [the Code Styles](style.md)
* [`requirements.txt`](//requirements.txt) contains versions of Python packages used as build tools

## GitHub Action Cache

The [`.github/workflows/ci.yaml`](//.github/workflows/ci.yaml) uses the
https://github.com/actions/cache to speed up the Continuous Integration (CI) builds of PRs on GitHub.

It is **normal** and intentional that every time the files listed above are changed this cache is _entirely_ invalidated, and thus a longer "full build" runs. PRs which do not change dependencies will still build noticeably faster.

If you are surprised and it seems to you that this cache does not work, because you noticed that the
re-build of the `main` branch after the merge of a GitHub Pull Request (PR) which changed dependencies
does not re-use the cache from the PR, this is also **expected** and not a bug.

It **WILL** work and speed up future PRs (provided there are no changes to dependencies again).

[GitHub's documentation](https://docs.github.com/en/actions/using-workflows/caching-dependencies-to-speed-up-workflows#restrictions-for-accessing-a-cache)
explains why this is so; TL;DR: a PR build can use the cache from a `main` build, but a `main` build will not use a PR's cache.

https://github.com/actions/cache/pull/575/files has some related discussion.

## Bill of Materials (BOM)

### Java

```text
{% include "./dependencies.txt" %}
```
