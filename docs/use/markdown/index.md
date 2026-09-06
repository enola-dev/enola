<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2026 The Enola <https://enola.dev> Authors

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

# Markdown 📄

`markdown` provides tools for processing Markdown documentation trees according to the
[Enola Knowledge Format (EKF)](https://wiki.enola.dev/computer/ai/format/ekf.html) and related specifications such as
[Markdown Magic Links](../../specs/markdown-magic-link/index.md).

It supports in-place canonical formatting, resolving internal magic links, generating dynamic directory index pages,
and converting Markdown documentation hierarchies into static HTML sites with bundled assets.

## Screencast

<asciinema-player src="script.cast" cols="80" rows="25"></asciinema-player>

## format

`format` (aliases: `fmt`) canonicalizes and formats Markdown files in place:

* Formats Markdown tables with cleanly aligned columns.
* Normalizes Markdown formatting according to canonical standards.
* Recursively processes directories or formats a single file.

```bash cd ../.././..
$ ./enola markdown format --help
...
```

See also [`enola canonicalize`](../canonicalize/index.md#markdown).

## generate-md

`generate-md` resolves magic links and generates category indexes for Markdown trees:

* Ingests source Markdown files from the input directory.
* Synthesizes `index.md` category landing pages for directories.
* Resolves magical links (`[[...]]`) into standard Markdown links with automatically extracted titles.
* Copies non-Markdown asset files to the output directory.

```bash cd ../.././..
$ ./enola markdown generate-md --help
...
```

## generate-html

`generate-html` generates static HTML documentation sites from Markdown files:

* Ingests source Markdown files and resolves magic links and indexes in memory.
* Converts Markdown files into standalone HTML pages with styling, navigation, Mermaid diagrams support, and syntax highlighting.
* Adds an Edit link pointing to the configured `EDIT-BASE-URL` (e.g. on GitHub).
* Bundles `wiki.css`, `wiki.js`, Mermaid scripts, and copies other asset files into the output directory.

```bash cd ../.././..
$ ./enola markdown generate-html --help
...
```

## CLI Options

```bash cd ../.././..
$ ./enola help markdown
...
```
