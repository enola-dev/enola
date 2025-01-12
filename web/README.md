<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2025 The Enola <https://enola.dev> Authors

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

# `enola/web` README

## Usage

1. One time `npm install` (TODO: Replace with Bazel...)

1. Regularly `npx tsc && npm run build` (TODO: Integrate better... e.g. using concurrently or npm-run-all?)

1. Start `./enola server --load "models/enola.dev/**.ttl" --httpPort=9090`

1. Open <http://0.0.0.0:9090/wui/index.html>

TODO Resolve CORS in  `npm run serve` to work with Enola JSON API server on another port.

TODO Support a _Dev_ mode where `npm run dev` launches `build.mjs` (TBD `.ts`) with
a `--watch` for _both_ `tsc` and `esbuild` and then does a `npm run serve` equivalent.

## NeXT

[ToDo](ToDo.md) has things to do for `web`/` (other TODO are elsewhere).
