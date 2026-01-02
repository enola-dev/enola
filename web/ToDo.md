<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2025-2026 The Enola <https://enola.dev> Authors

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

1. https://bun.sh/docs/bundler/fullstack#using-tailwindcss-in-html-routes for chat.html
1. Fix CORS ?
1. Use https://bun.sh/docs/install/workspaces to have a `package.json` e.g. for the Prettier version at the root, and have the one in `web/package.json`

## Functional

* Try out https://graphology.github.io/standard-library/layout-noverlap
* Do coloring using https://graphology.github.io/standard-library/communities-louvain; see https://gemini.google.com/app/4e3c639fc5213673

* Exploratory Mode... like http://en.lodlive.it, with UX as follows: Hide edges, until Node is clicked.

* Switch from GEXF to ~~JSON~~ https://rdf.js.org
* Show Edge labels!
* Show fixed type colors (like Graphviz already does)

* Highlight when hovering over label as well, not just dot
* Hover over Nodes should highlight all its edges

* Merge with //java/dev/enola/web/resources/static/main.css and show green NavBar
* Click on node should open Enola details page on the right

* Dark Modus support! ;-)

## Technical

### Bazel

* Use https://github.com/aspect-build/rules_js ... would have worked for `pnpm`, but won't for `bun`...
  but perhaps a simple GenRule, with some https://github.com/bazel-contrib/bazel-lib/blob/main/docs/write_source_files.md magic?
  Maybe quite simple, see https://gemini.google.com/app/9e87284a1100a011.

### Testing

* Web Test would be nice... at least just DOM, at first? Then, or directly, Browser?
    * Split Graphology & Sigma related code in x2 separate .ts; and test Graphology GEXF initial coordinates in Node, without Browser?
    * Try https://playwright.dev for web UI testing? With https://github.com/GoogleChromeLabs/chrome-for-testing?
* Make `bazel test //web` run web tests

### Performance

* Fix lack of "never cache" header support on GitHub pages by hosting the demo app elsewhere
    * https://gist.github.com/maximebories/961f12101af369804d40d5ec287e562b probably won't work?
    * E.g. Netlify or Cloudflare Pages, or maybe on IPFS with Pinata
    * https://developers.cloudflare.com/pages/configuration/headers/

### Maintainability

* Include `VERSION` (with Git rev) into front-end, probably using https://bun.sh/docs/bundler#env ?
* Enable https://github.com/dependabot/dependabot-core/issues/6528, once that's released
* Recheck if https://github.com/oven-sh/bun/issues/1760 got implemented, to avoid "seeing" transitive (only) dependencies in `import`
