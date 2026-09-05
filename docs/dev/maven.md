<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2024-2026 The Enola <https://enola.dev> Authors

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

# Maven repo

Enola is primarily a "fully packaged" application, intended for end-users.

Java developers can also use [our code](common.md) from our Maven repository at `https://docs.enola.dev/maven-repo/`.

Accessing the aforementioned URL in a Web Browser will show a 404 (because there is no `index.html`), but Maven-like build tools can download artifacts from there.

We publish the aggregate application artifact `dev.enola:enola:0.0.1-SNAPSHOT` as well as finer-grained modular libraries, starting with `dev.enola:common:0.0.1-SNAPSHOT`. Additional modules can be exported from Bazel by using the `java_module` macro with `export_maven = "<artifactId>"` in their `BUILD` file. Please [open an issue](https://github.com/enola-dev/enola/issues) to request making other packages available if you need them as standalone libraries.

The dependencies of our JARs are either on Maven Central or JitPack; your Maven resolver needs to be configured for both.

Both the [JBang demo](jbang.md) and the [launch via JBang](../use/index.md#jbang) use this repo.

[See the JavaDoc](javadoc/index.html).
