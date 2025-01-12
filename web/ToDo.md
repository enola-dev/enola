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

* ~~Use Enola Server `/gexf` instead of `arctic.gexf` (and delete that)~~
* Animate not "live & visible" after load (with Stop button) instead hard-coded iterations: 500,
  see https://graphology.github.io/standard-library/layout-forceatlas2.html#webworker
* Play with https://graphology.github.io/standard-library/layout-forceatlas2.html#settings
* Try out https://graphology.github.io/standard-library/layout-noverlap
* Do coloring using https://graphology.github.io/standard-library/communities-louvain; see https://gemini.google.com/app/4e3c639fc5213673

* Exploratory Mode... like http://en.lodlive.it, with UX as follows: Hide edges, until Node is clicked.

* Switch from GEXF to JSON
* Show Edge labels!
* Show fixed type colors (like Graphviz already does)

* Highlight when hovering over label as well, not just dot
* Let users drag nodes around
* Hover over Nodes should highlight all its edges
* Click on node should open Enola details page on the right
* Dark Modus support

## Visual

* Introduce a CSS so that controls are in a single row, with a nicer font
* `<div id="container">` should fill entire available space
* There shouldn't be any space around it

## Technical

### Clean

* Caching problems... how can I make it never cache bundle.js? I guess live edit will solve that..
* mv script.ts src/web/
* mv build.mjs src/dev/ (?)
* Needing `web/public/bundle.js` in `.gitignore` is ugly; how to better separate, to avoid?

### Productivity

* Does adopting https://eslint.org still make sense with TS? What does it add?
* How to auto rebuild & reload in browser on file change?

### TypeScript

* ~~Convert `script.js` to `script.[m?]ts`~~

### Bazel

* Use https://github.com/aspect-build/rules_js

### Testing

* Split Graphology & Sigma related code in x2 separate .ts; and test Graphology GEXF initial coordinates in Node, without Browser?
* `npm run test` How to do web UI testing? Try https://playwright.dev? With https://github.com/GoogleChromeLabs/chrome-for-testing?
* Make `bazel test //web` run web tests

### Performance

* #LATER
