<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2024 The Enola <https://enola.dev> Authors

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

# JBang

Java developers (not end-users) can use Enola via [JBang](https://www.jbang.dev/).

<!-- TODO [`learn/jbang`](https://github.com/enola-dev/enola/tree/main/learn/jbang) --> `learn/jbang`
has an example project illustrating how this works; clone and go there, and then:

1. `tools/maven/install.bash`
1. `cd learn/jbang`
1 `./jbang hello.java` will run an example using Enola
1. `./jbang edit --sandbox --open=code hello.java` opens an IDE

<!-- TODO Improve JBang integration:
        1. Build Enola on JitPack, and remove the need for step #1.
        2. Move `learn/jbang` out into a separate Git repo?
(But then how to test it in CI?
A Monorepo is very nice for that.
Perhaps just write a script to automagically extract (copy) it into a small repo?
-->
