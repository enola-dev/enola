<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2024 The Enola <https://enola.dev> Authors

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

# Other Tools

## SRE & DevOps

Enola 🕵🏾‍♀️ is not an Observability / Monitoring _(Application Performance Management, APM)_ tool. There are already many great ones, e.g. Prometheus, [Thanos](https://thanos.io), etc.
On the contrary, Enola is often set-up to integrate with and read data from existing monitoring tools,
if available.

Enola 🕵🏾‍♀️ is not an Incident Management or Postmortem tool.
On the contrary, it could automate creating incidents in such tools.

Enola 🕵🏾‍♀️ is not Configuration Management tool.

Enola 🕵🏾‍♀️ is not a Communication tool.

## Knowledge Management

<!-- I would be fun to capture in a structured format, with Enola... ;-) And then generated this from that! -->

### Linked Data Sources

* [Wikidata](https://www.wikidata.org) ([SPARQL query](https://query.wikidata.org))
* [European data](https://data.europa.eu)
* [LINDAS](https://lindas.admin.ch) the Swiss Governments's Ecosystem (also [opendata.swiss](https://opendata.swiss)?)
* [Musicbrainz's LinkedBrainz](https://wiki.musicbrainz.org/LinkedBrainz)
* [qudt.org](https://www.qudt.org) has RDF for [UCUM OoM](https://ucum.org) units

### Personal

Enola 🕵🏾‍♀️  may in the future have some [Personal Knowledge Management](https://en.wikipedia.org/wiki/Personal_knowledge_management) like features, similar to tools such as:

* [Obsidian](https://obsidian.md) #freeware
* [Logseq](https://logseq.com) #opensource
* [Cosma](https://cosma.arthurperret.fr) #opensource
* [Org Mode](https://orgmode.org) #opensource
* [The Brain](https://thebrain.com)
* [TiddlyWiki](https://tiddlywiki.com)
* [Zettlr](https://www.zettlr.com) #opensource
* [The Archive from zettelkasten.de](https://zettelkasten.de/the-archive/)
* [Zettelkasten history on Wikipedia](https://en.wikipedia.org/wiki/Zettelkasten)

## Graph

### Visualization

Enola 🕵🏾‍♀️  visualizes the relationships of its _Entities_ using:

* [Graphviz](https://graphviz.org) #available
* [Gephi](https://gephi.org), with [Gephi Lite](https://gephi.org/gephi-lite/) which uses [SigmaJS](https://www.sigmajs.org) on [Graphology](https://graphology.github.io) for JS #planned #[FOSDEM](https://github.com/vorburger/vorburger.ch-Notes/blob/develop/conferences/FOSDEM-2024.md)

### Frameworks

* [`com.google.common.graph`](https://github.com/google/guava/wiki/GraphsExplained) Java API
* [JGraphT](https://jgrapht.org) Java Library

[Semantic Triple](https://en.wikipedia.org/wiki/Semantic_triple) Java libraries ([comparison](https://github.com/trellis-ldp/trellis/issues/358)):

* [Eclipse RDF4j](https://rdf4j.org)
* [Apache Jena](https://jena.apache.org)
* [Apache Commons RDF API](https://commons.apache.org/proper/commons-rdf/)

## Persistence

Enola 🕵🏾‍♀️ might not ever become a (persistent) "database" itself - but could integrate with some in the future?

### [Triplestore (AKA RDF store) DB](https://en.wikipedia.org/wiki/Triplestore)

Some [db-engines.com](https://db-engines.com/en/ranking/rdf+store):

* [GraphDB, by Ontotext](https://graphdb.ontotext.com) #freemium #commercial
* [Apache Jena's TDB](https://jena.apache.org) #opensource
* [Oxigraph](https://github.com/oxigraph/oxigraph) #opensource
* [Stardog](https://www.stardog.com) #commercial
* [Virtuoso](https://virtuoso.openlinksw.com) #opensource
* [Blazegraph](https://blazegraph.com) #opensource
* [Halyard](https://merck.github.io/Halyard/) (by [Merck](https://github.com/merck)) #opensource
* [Strabon](http://strabon.di.uoa.gr) #opensource

### [Graph DB](https://en.wikipedia.org/wiki/Graph_database)

* [Neo4j](https://neo4j.com), with [neosemantics](https://neo4j.com/labs/neosemantics/) for RDF
* [Tinkerpop](https://tinkerpop.apache.org)
* [Other Graph databases](https://en.wikipedia.org/wiki/Graph_database#List_of_graph_databases)...

### Vector DB

Vector _("Embedding")_ databases of possible future interest:

* [ChromaDB](https://www.trychroma.com)
* [Weaviate](https://weaviate.io)

## Symbolic AI Reasoning (GOFAI?)

[Semantic Reasoning](https://en.wikipedia.org/wiki/Semantic_reasoner) through [modus ponens](https://en.wikipedia.org/wiki/Modus_ponens) of an [Inference Engine](https://en.wikipedia.org/wiki/Inference_engine) by
_Forward Chaining; also see Backward Chaining, Backtracking, Backpropagation -
[TBD](https://en.wikipedia.org/wiki/Symbolic_artificial_intelligence)._

## Build

Enola 🕵🏾‍♀️ models may internally gain (optional) `hash` support, for caching transformations; this would give it some aspects that make it similar to [tools which made the art of procrasting while running builds](https://xkcd.com/303/), such as:

* [Justbuild](https://github.com/just-buildsystem/justbuild) (not to be confused with [just.systems](https://just.systems) and similar [command runners](https://github.com/casey/just?tab=readme-ov-file#alternatives-and-prior-art))
* [Bazel](https://bazel.build) (and its flattering imitations, such as [Buck2](https://buck2.build) and [Pants](https://www.pantsbuild.org))
