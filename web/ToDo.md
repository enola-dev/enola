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

## Tech

1. Apply https://bun.sh/docs/typescript#suggested-compileroptions
1. Fix `bun tsc`
1. Adopt https://bun.sh/docs/bundler/fullstack ... and fix CORS!
1. Either rename web-out/index*.html to `index.html` in `build.ts`,
   OR make `../enola server` send either (better) `Cache-Control: no-cache` & `ETag: "abcdef1234"`
   (or just `Cache-Control: no-store`; or `max-age=0`, really same?)

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

* Make pre-commit add License Header to `*.ts`
* Caching problems... how can I make it never cache bundle.js? Will Watch Mode solve that? Else [hash] in JS filename...
* Needing `web/public/bundles/` in `.gitignore` is a bit ugly; how is this normally better separated?

### Productivity

* Does adopting https://eslint.org still make sense with TS? What does it add? See https://typescript-eslint.io
* How to auto rebuild & reload in browser on file change?

### Bazel

* Use https://github.com/aspect-build/rules_js ... would have worked for `pnpm`, but won't for `bun`...
  but perhaps a simple GenRule, with some https://github.com/bazel-contrib/bazel-lib/blob/main/docs/write_source_files.md magic?

### Testing

* Web Test would be nice... at least just DOM, at first? Then, or directly, Browser?
  * Split Graphology & Sigma related code in x2 separate .ts; and test Graphology GEXF initial coordinates in Node, without Browser?
  * Try https://playwright.dev for web UI testing? With https://github.com/GoogleChromeLabs/chrome-for-testing?
* Make `bazel test //web` run web tests

### Performance

* #LATER

### Maintainability

* Include `VERSION` (with Git rev) into front-end, probably using https://bun.sh/docs/bundler#env ?
* Enable https://github.com/dependabot/dependabot-core/issues/6528, once that's released
* Recheck if https://github.com/oven-sh/bun/issues/1760 got implemented, to avoid "seeing" transitive (only) dependencies in `import`
