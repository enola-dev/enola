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

# JSON LD UI Roadmap ToDo

<!-- TODO Merge this with a TBD ../docs/?/roadmap.md? -->

## Functional #what

* Build v1.0 MVP POC
* Read JSON LD URL from static demo page URL; e.g. via fragment, like e.g. on https://validator.schema.org/#url=https%3A%2F%2Fjson-ld.org%2F
* The JSON LD loaded from the URL should be "on the page" so that e.g. https://validator.schema.org and maybe https://search.google.com/test/rich-results "sees" it... is it enough to just dynamically inject it into a `<script type="application/ld+json">` tag, like https://developers.google.com/search/docs/appearance/structured-data/generate-structured-data-with-javascript#custom-javascript? Try!
* Advertise, e.g. on https://json-ld.org

## Technical #how

* `p dev` with https://www.typescriptlang.org/docs/handbook/compiler-options.html `--watch`?
* Mustache Template?
* `p dev` with web server? How to stop both Web Server and TSC Watch on Ctrl-C?
* How to test, simplest? ede.https://github.com/pre-commit/mirrors-eslint
* https://www.typescriptlang.org/docs/handbook/configuring-watch.html?
* https://www.typescriptlang.org/docs/handbook/tsconfig-json.html
* https://www.typescriptlang.org/docs/handbook/gulp.html?
* https://www.typescriptlang.org/docs/handbook/babel-with-typescript.html?
* WASM instead of JS from TS?!
* https://github.com/awebdeveloper/pre-commit-tslint?
* https://github.com/pre-commit/mirrors-eslint? (Unless that's already in package.yaml now.)
* Validate HTML dynamically built in a browser during a build test; see https://github.com/vorburger/Notes/blob/master/Reference/html.md; https://html-validate.org/frameworks/jest.html looks interesting & useful for this?
