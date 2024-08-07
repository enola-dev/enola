#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2024 The Enola <https://enola.dev> Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -euox pipefail

rm -rf docs/models/ .built/linkml/

tools/protoc/protoc.bash

find models/ -type d -exec mkdir -p docs/{} \;
find models/ -type f -exec ln -f {} docs/{} \;
rm docs/models/{BUILD,build.bash}

# https://linkml.io
mkdir -p .built/linkml/
find docs/models/ -name "*.linkml.yaml" -print0 | xargs -n 1 -0 linkml-lint --validate --config models/.linkmllint.yaml
find docs/models/ -name "*.linkml.yaml" -print0 | xargs -n 1 -0 gen-project -X prefixmap -X owl -X shacl -d .built/linkml/
# TODO Re-enable OWL after fixing https://github.com/enola-dev/enola/issues/759
# TODO Drop all *.linkml.* e.g. to rename file.linkml.context.jsonld to just file.linkml.context.jsonld
# TODO --no-mergeimports? https://linkml.io/linkml/schemas/imports.html#making-merged-files-for-distribution ?
# TODO https://linkml.io/linkml/generators/linkml.html ?
mv .built/linkml docs/models/

# TODO Support --load *.rdf application/rdf+xml : That's quite simple, really; just requires piping through RIO.
# TODO Support --load *.owl : Could map it into enola.meta.Schema? But... what's the priority of this, really?
./enola -vvv docgen --load="docs/models/**.{ttl}" --output=docs/models/

# TODO Support GLOBs in rosetta like in docgen? (Low priority, because DocGen will gen. embedded JSON-LD anyway.)
./enola -v rosetta --in=models/enola.dev.ttl --out=docs/models/enola.dev.jsonld

# TODO RDF* --load="models/**.ttl[s?]"
