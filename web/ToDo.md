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

# Enola `enola/web` TODO

## Functional

* Use Enola Server `/gexf` instead of `arctic.gexf` (and delete that)
* Let users drag nodes around
* Click on node should open Enola details page on the right

## Visual

* Introduce a CSS so that controls are in a single row, with a nicer font
* `<div id="container">` should fill entire available space
* There shouldn't be any space around it

## Technical

### Clean

* Needing `web/public/bundle.js` in `.gitignore` is ugly; how to better separate, to avoid?

### Productivity

* How to auto rebuild & reload in browser on file change?

### TypeScript

* Convert `script.js` to `script.[m?]ts`

### Bazel

* Use https://github.com/aspect-build/rules_js

### Testing

* `npm run test` How to do web UI testing?
* Make `bazel test //web` run web tests

### Performance

* #LATER
