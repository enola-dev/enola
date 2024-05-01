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

<!-- TODO https://github.com/enola-dev/dora/tree/main/awesome:
     It would be fun to capture in a structured format, with Enola... ;-)
     Actually this may be a (kind of) "citations" background?! And e.g. Zotero has RDF... hm!
     This is kind of like an https://github.com/sindresorhus/awesome/blob/main/awesome.md ?
     So, ... perhaps https://github.com/enola-dev/awesome-linked-data ? Research existing?
     Then generate this MD from that?  -->

### Linked Data Sources

* [Data Commons](https://datacommons.org), by #Google
* [Wikidata](https://www.wikidata.org) ([SPARQL query](https://query.wikidata.org))
* [European data](https://data.europa.eu), and [Kohesio](https://kohesio.ec.europa.eu)
* [LINDAS](https://lindas.admin.ch) the Swiss Governments's Ecosystem (also [opendata.swiss](https://opendata.swiss)?)
* [SBB](https://data.sbb.ch) The Swiss trains schedule
* [Musicbrainz's LinkedBrainz](https://wiki.musicbrainz.org/LinkedBrainz)
* [qudt.org](https://www.qudt.org) has RDF for [UCUM OoM](https://ucum.org) units

#### Academic

* [ORCID](https://orcid.org)
* [OpenAIRE](https://www.openaire.eu)

### Vocabularies

* [SPDX](https://spdx.dev) Software Package Data Exchange (evolution of [DOAP](https://github.com/ewilderj/doap/wiki), see [Maven](https://maven.apache.org/plugins/maven-doap-plugin/))
* [Digital Buildings](https://google.github.io/digitalbuildings/) #Google

### Platforms (?)

* [Protégé](https://protege.stanford.edu) #opensource #editor #ontology #ToDo
* [Corese](https://project.inria.fr/corese/) #opensource #ToDo
* [PoolParty](https://www.poolparty.biz) #commercial #LLM
* [Anzo, by Cambridge Semantics](https://cambridgesemantics.com/anzo-platform/) #commercial
* [Semaphore, by Progress](https://www.progress.com/semaphore) #commercial
* [metaphactory, by Metaphacts](https://metaphacts.com)

### Personal

Enola 🕵🏾‍♀️  may in the future have some [Personal Knowledge Management](https://en.wikipedia.org/wiki/Personal_knowledge_management) like features, similar to tools such as:

* [Obsidian](https://obsidian.md) #freeware
* [Logseq](https://logseq.com) #opensource #ToDo
* [Cosma](https://cosma.arthurperret.fr) #opensource
* [Org Mode](https://orgmode.org) #opensource
* [The Brain](https://thebrain.com)
* [Roam Research](https://roamresearch.com) #ToDo
* [RemNote](https://www.remnote.com)
* [TiddlyWiki](https://tiddlywiki.com)
* [Zettlr](https://www.zettlr.com) #opensource
* [The Archive from zettelkasten.de](https://zettelkasten.de/the-archive/)
* [Zettelkasten history on Wikipedia](https://en.wikipedia.org/wiki/Zettelkasten)

## Graph

### Visualization

Enola 🕵🏾‍♀️ visualizes the relationships of its _Entities_ using:

* [yEd](https://www.yworks.com) (yFiles) #commercial #sdk #freemium
* [Graphviz](https://graphviz.org) #available
* [Gephi](https://gephi.org), with [Gephi Lite](https://gephi.org/gephi-lite/) which uses [SigmaJS](https://www.sigmajs.org) on [Graphology](https://graphology.github.io) for JS #[FOSDEM](https://github.com/vorburger/vorburger.ch-Notes/blob/develop/conferences/FOSDEM-2024.md) #planned #ToDo
* [D3js.org](https://d3js.org) also has [Graphs](https://observablehq.com/@d3/force-directed-graph-component?collection=@d3/charts) (and [d3rdf](https://github.com/Rathachai/d3rdf))

Other _"Graph Explorer"_ kind of UIs that we have heard about include:

* [aws/graph-explorer](https://github.com/aws/graph-explorer) #RDF #SPARQL #TinkerPop #AWS #opensource
* [GraphDB Workbench](https://github.com/Ontotext-AD/graphdb-workbench) #RDF4j #freemium #commercial #opensource
* [zazuko/graph-explorer](https://github.com/zazuko/graph-explorer) #RDF #SPARQL #opensource #[fork](https://github.com/metaphacts/ontodia/network/members) #metaphacts/ontodia

Web-based:

* **[lodlive.it](http://en.lodlive.it/?https://w3id.org/italia/env/ld/place/municipality/00201_042002)** #RDF
* [isSemantic's RDF Visualizer](https://issemantic.net/rdf-visualizer)
* [Triply's Yasgui](https://docs.triply.cc/yasgui-api/)
* [Zazuko's Trifid](https://github.com/zazuko/trifid)
* [Zazuko's Sketch](https://sketch.zazuko.com) (also [for VSC](https://marketplace.visualstudio.com/items?itemName=Zazuko.vscode-rdf-sketch)) #RDF #swiss
* [VisGraph^3](https://visgraph3.github.io) #opensource #RDF
* [Brickschema's Viewer](https://viewer.brickschema.org)

Vaguely related other such tools include:

* [Reactome](https://en.wikipedia.org/wiki/Reactome)'s [Pathway Browser](https://reactome.org/PathwayBrowser)

### Frameworks

* [`com.google.common.graph`](https://github.com/google/guava/wiki/GraphsExplained) Java API
* [JGraphT](https://jgrapht.org) Java Library

[Semantic Triple](https://en.wikipedia.org/wiki/Semantic_triple) Java libraries ([comparison](https://github.com/trellis-ldp/trellis/issues/358)):

* [Eclipse RDF4j](https://rdf4j.org)
* [Apache Jena](https://jena.apache.org)
* [Apache Commons RDF API](https://commons.apache.org/proper/commons-rdf/)

## Documentation Generation

* [Snowman](https://github.com/glaciers-in-archives/snowman) _static site generator for SPARQL_

## Persistence

Enola 🕵🏾‍♀️ might not ever become a (persistent) "database" itself - but could integrate with some in the future?

### [Triplestore (AKA RDF store) DB](https://en.wikipedia.org/wiki/Triplestore)

Some [db-engines.com](https://db-engines.com/en/ranking/rdf+store):

* [qEndpoint](https://github.com/the-qa-company/qEndpoint) #RDF #RDF4j #HDT #GeoSPARQL #SPARQL #opensource
* [GraphDB, by Ontotext](https://graphdb.ontotext.com) #freemium #commercial
* [Apache Jena's TDB](https://jena.apache.org) #opensource
* [Oxigraph](https://github.com/oxigraph/oxigraph) #opensource
* [Stardog](https://www.stardog.com) #freemium #commercial #GraphML? #LLM?
* [Virtuoso](https://virtuoso.openlinksw.com) #opensource
* [Blazegraph](https://blazegraph.com) #opensource
* [Halyard](https://merck.github.io/Halyard/) (by [Merck](https://github.com/merck)) #opensource
* [Cayley](https://cayley.io) #opensource #Google
* [Strabon](http://strabon.di.uoa.gr) #opensource
* [BadWolf](https://github.com/google/badwolf) #temporal #Google #opensource
* [Neptune](https://aws.amazon.com/neptune/) #AWS #cloud #commercial #SaaS
* [Cosmos](https://azure.microsoft.com/en-us/products/cosmos-db/) #Azure #cloud #commercial #SaaS

### [Graph DB](https://en.wikipedia.org/wiki/Graph_database)

* [Tinkerpop](https://tinkerpop.apache.org) #opensource
* [JanusGraph](https://janusgraph.org) #opensource
* [Neo4j](https://neo4j.com), with [neosemantics](https://neo4j.com/labs/neosemantics/) for RDF
* [Tiger](https://www.tigergraph.com) #commercial #ML
* [Other Graph databases](https://en.wikipedia.org/wiki/Graph_database#List_of_graph_databases)...

### GraphML DB

* [LlamaIndex](https://www.llamaindex.ai) #GraphML #opensource? #ToDo
* [varunshenoy/GraphGPT](https://github.com/varunshenoy/GraphGPT) #GraphML #opensource #demo
* [HKUDS/GraphGPT](https://github.com/HKUDS/GraphGPT) #GraphML #opensource #paper
* [Kumo.ai](https://kumo.ai) #GraphML #commercial #LLM
* [Relational.ai](https://relational.ai) #commercial
* [QAnswer](https://qanswer.ai) by [The QA Company](https://the-qa-company.com) (on [GitHub](https://github.com/the-qa-company)) #RDF
* [Franz](https://franz.com) #commercial

### Vector DB

Vector _("Embedding")_ databases of possible future interest:

* [ChromaDB](https://www.trychroma.com)
* [Weaviate](https://weaviate.io)

### Document DB

* [CursusDB](https://github.com/cursusdb/cursusdb) #opensource
* [MongoDB](https://www.mongodb.com) #commercial
* [CouchDB](https://couchdb.apache.org) #opensource

## AI

### Symbolic AI Reasoning (GOFAI?)

[Semantic Reasoning](https://en.wikipedia.org/wiki/Semantic_reasoner) through [modus ponens](https://en.wikipedia.org/wiki/Modus_ponens) of an [Inference Engine](https://en.wikipedia.org/wiki/Inference_engine) by
_Forward Chaining; also see Backward Chaining, Backtracking, Backpropagation -
[TBD](https://en.wikipedia.org/wiki/Symbolic_artificial_intelligence)._

* [Mangle](https://github.com/google/mangle) #OpenSource #Google

## Platforms

### Enterprise Data Catalogs

* [data.world](https://data.world)

## Model Template Languages

* [CUE](https://cuelang.org)
* [Yglu](https://yglu.io)

## Build

Enola 🕵🏾‍♀️ models may internally gain (optional) `hash` support, for caching transformations; this would give it some aspects that make it similar to [tools which made the art of procrasting while running builds](https://xkcd.com/303/), such as:

* [Bob](https://bob.build)
* [Justbuild](https://github.com/just-buildsystem/justbuild) (not to be confused with [just.systems](https://just.systems) and similar [command runners](https://github.com/casey/just?tab=readme-ov-file#alternatives-and-prior-art))
* [Bazel](https://bazel.build) (and its flattering imitations, such as [Buck2](https://buck2.build) and [Pants](https://www.pantsbuild.org))

## Conferences & Events

### Switzerland

* [Linked Data Days](https://www.bfh.ch/de/aktuell/fachveranstaltungen/linked-data-day-2024/) in Bern
* [Knowledge Graph Forum](https://github.com/zazuko/knowledge-graph-forum) in Basel
* [Data Community Conference](https://www.data-community.ch) in Bern

### International

* [Knowledge Graph Conference](https://www.knowledgegraph.tech)
