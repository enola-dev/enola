#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

rm -rf docs/models/

tools/protoc/protoc.bash

find models/ -type d -exec mkdir -p docs/{} \;
find models/ -type f -exec ln -f {} docs/{} \;
rm docs/models/{BUILD,build.bash}

# TODO Parent directories' Things are missing? Perhaps load them "on-demand" automagically, with flag?
# TODO Support --load *.rdf application/rdf+xml : That's quite simple, really; just requires piping through RIO.
# TODO Support --load *.owl : Could map it into enola.meta.Schema? But... what's the priority of this, really?
./enola -vvv docgen --load="docs/models/**.{ttl}" --load=enola:TikaMediaTypes --output=docs/models/
dot -Tsvg -O docs/models/graphviz.gv

# TODO Support GLOBs in rosetta like in docgen? (Low priority, because DocGen will gen. embedded JSON-LD anyway.)
./enola -v rosetta --in=models/enola.dev.ttl --out=docs/models/enola.dev.jsonld

# NB: --no-file-loader only marginally helps to make the picture clearer; what we need is real sparql: query support, to filter!
# TODO Merge these two (once it works & looks well enough); this might need adding support for multiple repeating --load arguments...
# But for the "smaller" one, the default "dot" layout looks better; for the "full" one, the sfdp https://en.wikipedia.org/wiki/Force-directed_graph_drawing
# looks "better" (kind of; but not really, it perhaps would be if it were more "dynamic"?).
./enola rosetta --no-file-loader --in models/enola.dev/mediaTypes.ttl --out="docs/models/enola.dev/mediaType/graph.gv?full=true" && dot -Tsvg -O docs/models/enola.dev/mediaType/graph.gv
./enola -v gen graphviz --no-file-loader --load=enola:TikaMediaTypes --output docs/models/enola.dev/mediaType/ && dot -Ksfdp -Tsvg -O docs/models/enola.dev/mediaType/graphviz.gv

# TODO RDF* --load="models/**.ttl[s?]"
