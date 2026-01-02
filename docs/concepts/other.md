<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2026 The Enola <https://enola.dev> Authors

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

[Awesome Semantic Web](https://github.com/semantalytics/awesome-semantic-web) has similar related links.

## SRE & DevOps

Enola 🕵🏾‍♀️ is not an Observability / Monitoring _(Application Performance Management, APM)_ tool. There are already many great ones, e.g. Prometheus, [Thanos](https://thanos.io), etc.
On the contrary, Enola is often set-up to integrate with and read data from existing monitoring tools,
if available.

Enola 🕵🏾‍♀️ is not an Incident Management or Postmortem tool.
On the contrary, it could automate creating incidents in such tools.

Enola 🕵🏾‍♀️ is not Configuration Management tool.

Enola 🕵🏾‍♀️ is not a Communication tool.

## RDF Linked Data Tools

* [`rdflint`](https://imas.github.io/rdflint/) #RDF #lint #validate #opensource #java
* [Ontospy](https://lambdamusic.github.io/Ontospy/) #python #CLI #RDFS #OWL #SKOS #documentation #generation #ToDo
* [Iolanta Linked Data browser](https://iolanta.tech) #ToDo

## Knowledge Management

<!-- TODO //models/enola.dev/other.ttl as source of this!

     As outlined in https://github.com/enola-dev/dora/tree/main/awesome:
     It would be fun to capture in a structured format, with Enola... ;-)
     Actually this may be a (kind of) "citations" background?! And e.g. Zotero has RDF... hm!
     This is kind of like an https://github.com/sindresorhus/awesome/blob/main/awesome.md ?
     So, ... perhaps https://github.com/enola-dev/awesome-linked-data ? Research existing?
     Then generate this MD from that?  -->

### Linked Data Sources

* [Data Commons](https://datacommons.org), by #Google
* [Wikidata](https://www.wikidata.org) ([SPARQL query](https://query.wikidata.org))
* [DBpedia](https://www.dbpedia.org)
* [European data](https://data.europa.eu), and [Kohesio](https://kohesio.ec.europa.eu)
* [LINDAS](https://lindas.admin.ch) the Swiss Governments's Ecosystem (also [opendata.swiss](https://opendata.swiss)?)
* [SBB](https://data.sbb.ch) The Swiss trains schedule
* [Musicbrainz's LinkedBrainz](https://wiki.musicbrainz.org/LinkedBrainz)
* [qudt.org](https://www.qudt.org) has RDF for [UCUM OoM](https://ucum.org) units
* [LOD Cloud](https://lod-cloud.net)

#### Academic

* [ORCID](https://orcid.org)
* [OpenAIRE](https://www.openaire.eu)

### Vocabularies

* [SPDX](https://spdx.dev) Software Package Data Exchange (evolution of [DOAP](https://github.com/ewilderj/doap/wiki), see [Maven](https://maven.apache.org/plugins/maven-doap-plugin/))
* [Digital Buildings](https://google.github.io/digitalbuildings/) #Google

### Platforms (?)

* [GraphAware](https://graphaware.com) #commercial
* [Stardog](https://www.stardog.com) #freemium #commercial #GraphML? #LLM?
* [Protégé](https://protege.stanford.edu) #opensource #editor #ontology #ToDo
* [Corese](https://project.inria.fr/corese/) #opensource #ToDo
* [PoolParty](https://www.poolparty.biz) #commercial #LLM
* [Anzo, by Cambridge Semantics](https://cambridgesemantics.com/anzo-platform/) #commercial
* [Semaphore, by Progress](https://www.progress.com/semaphore) #commercial
* [metaphactory, by Metaphacts](https://metaphacts.com)
* [openEngiadina](https://openengiadina.net) #opensource #swiss

### Personal

Enola 🕵🏾‍♀️  may in the future have some [Personal Knowledge Management](https://en.wikipedia.org/wiki/Personal_knowledge_management) like features, similar to tools such as:

With different _types_ of generic _objects:_

* [Anytype](https://anytype.io) #opensource #p2p
* [Notion](https://www.notion.com) #cloud #saas #commercial
* [Tana](https://tana.inc) #commercial #start-with-creditcard #ai
* [Treehouse](https://treehouse.sh) #opensource #framework

With _Notes_ primarily:

* [Foam](https://foambubble.github.io/foam/) #opensource #vsc
* [Dendron](https://github.com/dendronhq/dendron) #opensource #vsc
* [Obsidian](https://obsidian.md) #freeware
* [Logseq](https://logseq.com) #opensource #ToDo
* [deepdwn](https://billiam.itch.io/deepdwn) #commercial
* [Cosma](https://cosma.arthurperret.fr) #opensource
* [Org Mode](https://orgmode.org) #opensource
* [The Brain](https://thebrain.com)
* [Roam Research](https://roamresearch.com) #ToDo
* [RemNote](https://www.remnote.com)
* [TiddlyWiki](https://tiddlywiki.com)
* [Zettlr](https://www.zettlr.com) #opensource
* [The Archive from zettelkasten.de](https://zettelkasten.de/the-archive/)
* [Zettelkasten history on Wikipedia](https://en.wikipedia.org/wiki/Zettelkasten)

### Desktop Search

* [Desktop search on Wikipedia](https://en.m.wikipedia.org/wiki/Desktop_search), with [List](https://en.m.wikipedia.org/wiki/List_of_search_engines#Desktop_search_engines)

### OSINT

* [Maltego](https://www.maltego.com) #commercial

## Graph

### Visualization

Enola 🕵🏾‍♀️ visualizes (TBD) the relationships of its _Entities_ using:

* [yEd](https://www.yworks.com) (yFiles) #commercial #sdk #freemium
* [Graphviz](https://graphviz.org) #available
* [Gephi](https://gephi.org), with [Gephi Lite](https://gephi.org/gephi-lite/) which uses [SigmaJS](https://www.sigmajs.org) on [Graphology](https://graphology.github.io) for JS #[FOSDEM](https://github.com/vorburger/vorburger.ch-Notes/blob/develop/conferences/FOSDEM-2024.md) #dynamicGraph #web #active #planned #ToDo
* [D3js.org](https://d3js.org), and [Observable Plot](https://observablehq.com/plot/) has #[graph](https://observablehq.com/@d3/force-directed-graph-component?collection=@d3/charts) (and [d3rdf](https://github.com/Rathachai/d3rdf)) #[timeline](https://observablehq.com/@observablehq/plot-civilizations-timeline?intent=fork)
* [G6](https://github.com/antvis/G6) #opensource #graph #javascript
* [vis.js](https://visjs.org) #opensource #graph #timeline #web
* [Cytoscape](https://cytoscape.org) #opensource #graph #desktop #[plugins](https://apps.cytoscape.org/)
* [Cytoscape.js](https://js.cytoscape.org/) #graph #library #web
* [Mermaid](https://mermaid.js.org) #graph #[timeline](https://mermaid.js.org/syntax/timeline.html) #opensource
* [PlantUML](https://plantuml.com) #opensource
* [GraphStream](https://graphstream-project.org) #opensource #dynamicGraph #java #swing #desktop #inactive
* [Vega Lite](https://vega.github.io/vega-lite/) & [Vega](https://vega.github.io/vega/) #opensource
* [yuml](https://yuml.me) #UML
* [amCharts](https://www.amcharts.com/demos/force-directed-network/) #network #commercial
* [KronoGraph](https://cambridge-intelligence.com/kronograph/features/) #network #timeline #commercial
* [GoJS](https://gojs.net) #network #tree #commercial
* [Ruimdetijd/timeline](https://github.com/Ruimdetijd/timeline) #timeline #gpl #broken
* [Apache Charts](https://echarts.apache.org/examples/en/index.html#chart-type-graph) #graph #opensource @impronta48

Other _"Graph Explorer"_ kind of UIs that we have heard about include:

* [aws/graph-explorer](https://github.com/aws/graph-explorer) #RDF #SPARQL #TinkerPop #AWS #opensource
* [GraphDB Workbench](https://github.com/Ontotext-AD/graphdb-workbench) #RDF4j #freemium #commercial #opensource
* [zazuko/graph-explorer](https://github.com/zazuko/graph-explorer) #RDF #SPARQL #opensource #[fork](https://github.com/metaphacts/ontodia/network/members) #metaphacts/ontodia

Web-based:

* [Linkurious](https://linkurious.com) #Neo4j #Memgraph #ToDo
* **[lodlive.it](http://en.lodlive.it/?https://w3id.org/italia/env/ld/place/municipality/00201_042002)** is #RDF #[opensource](https://github.com/LodLive/LodLive)
* [isSemantic's RDF Visualizer](https://issemantic.net/rdf-visualizer)
* [Triply's Yasgui](https://docs.triply.cc/yasgui-api/)
* [Zazuko's Trifid](https://github.com/zazuko/trifid)
* [Zazuko's Sketch](https://sketch.zazuko.com) (also [for VSC](https://marketplace.visualstudio.com/items?itemName=Zazuko.vscode-rdf-sketch)) #RDF #swiss
* [VisGraph^3](https://visgraph3.github.io) #opensource #RDF
* [Brickschema's Viewer](https://viewer.brickschema.org)

Vaguely related other such tools include:

* [Reactome](https://en.wikipedia.org/wiki/Reactome)'s [Pathway Browser](https://reactome.org/PathwayBrowser)

### Graph Frameworks

* [`com.google.common.graph`](https://github.com/google/guava/wiki/GraphsExplained) Java API
* [JGraphT](https://jgrapht.org) Java Library
* [NetworkX](https://networkx.org/) Python package

[Semantic Triple](https://en.wikipedia.org/wiki/Semantic_triple) Java libraries ([comparison](https://github.com/trellis-ldp/trellis/issues/358)):

* [Eclipse RDF4j](https://rdf4j.org)
* [Apache Jena](https://jena.apache.org)
* [Apache Commons RDF API](https://commons.apache.org/proper/commons-rdf/)

Non-Java RDF libraries:

* [RDF JavaScript & TypeScript Libraries](https://rdf.js.org/) #RDF #JS #TS #library
* [Redland librdf](https://librdf.org/) #RDF #C #library

## Documentation Generation

* [LEXREX](https://lexrex.web.app) semantic vocabulary visual builder and manager, by [APICatalog.com](https://apicatalog.com)
* [Snowman](https://github.com/glaciers-in-archives/snowman) _static site generator for SPARQL_
* [Quartz](https://quartz.jzhao.xyz/) #static #site #MD #Wikilink #Obsidian #Roam #Mermaid #LaTeX #RSS #TS #ToDo

## Persistence

Enola 🕵🏾‍♀️ might not ever become a (persistent) "database" itself - but could integrate with some in the future?

### [Triplestore (AKA RDF store) DB](https://en.wikipedia.org/wiki/Triplestore)

Some [db-engines.com](https://db-engines.com/en/ranking/rdf+store):

* RDF4j (formerly known as OpenRDF Sesame) [In-Memory](https://rdf4j.org/javadoc/latest/org/eclipse/rdf4j/sail/memory/MemoryStore.html),
  or [native B-Tree](https://rdf4j.org/javadoc/latest/org/eclipse/rdf4j/sail/nativerdf/NativeStore.html),
  or [LMDB](https://rdf4j.org/documentation/programming/lmdb-store/),
  or [ElasticSearch](https://rdf4j.org/javadoc/latest/org/eclipse/rdf4j/sail/elasticsearchstore/ElasticsearchStore.html) #LMDB #SPARQL #RDF #RDF4j
* [TinySPARQL](https://gnome.pages.gitlab.gnome.org/tinysparql/) #SPARQL #RDF #SQLite #Linux #GNOME #Tracker #Miner
* [Tinkerpop](https://tinkerpop.apache.org) with [SPARQL-Gremlin](https://tinkerpop.apache.org/docs/current/reference/#sparql-gremlin) (also NB [Sqlg](https://www.sqlg.org)) #opensource
* [qEndpoint](https://github.com/the-qa-company/qEndpoint) #RDF #RDF4j #HDT #GeoSPARQL #SPARQL #opensource
* [GraphDB, by Ontotext](https://graphdb.ontotext.com) #freemium #commercial #RDF4j
* [Apache Jena's TDB](https://jena.apache.org) (Fuseki?) #opensource
* [Oxigraph](https://github.com/oxigraph/oxigraph) #opensource
* [Virtuoso](https://virtuoso.openlinksw.com) #opensource
* [Blazegraph](https://blazegraph.com) #opensource
* [Halyard](https://merck.github.io/Halyard/) (by [Merck](https://github.com/merck)) #opensource
* [Cayley](https://cayley.io) #opensource #Google
* [Strabon](http://strabon.di.uoa.gr) #opensource
* [BadWolf](https://github.com/google/badwolf) #temporal #Google #opensource
* [Neptune](https://aws.amazon.com/neptune/) #AWS #cloud #commercial #SaaS
* [Cosmos](https://azure.microsoft.com/en-us/products/cosmos-db/) #Azure #cloud #commercial #SaaS
* [4store](https://github.com/4store/4store) #opensource

### [Graph DB](https://en.wikipedia.org/wiki/Graph_database)

* [JanusGraph](https://janusgraph.org) #opensource
* [Neo4j](https://neo4j.com), with [neosemantics](https://neo4j.com/labs/neosemantics/) for RDF
* [Memgraph](https://memgraph.com) ##Memgraph #Neo4j #CypherQL #GraphDB #opensource #commercial #ToDo
* [Tiger](https://www.tigergraph.com) #commercial #ML
* [Other Graph databases](https://en.wikipedia.org/wiki/Graph_database#List_of_graph_databases)...

### GraphML DB

* [ArangoDB](https://arangodb.com) #GraphML #opensource #commercial
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

### AI Tools

* [Enola.dev Application](../use/index.md)
* [Gemini CLI](https://google-gemini.github.io/gemini-cli/)
* [Claude Code](https://www.anthropic.com/claude-code)
* [Chat GPT Desktop](https://chatgpt.com/features/desktop) #no-linux

### Agents Frameworks

* [Google Agent Development Kit](https://google.github.io/adk-docs/) #Google #LLM #agents
* [LangChain](https://langchain.com) #opensource #LLM #agents
* [Java AI Agents Frameworks](https://github.com/enola-dev/awesome-java-ai/?tab=readme-ov-file#agents)

### RAG

* [Kapa](https://www.kapa.ai) #graphRAG #SaaS #replicate #ToDo

### MCP

See [MCP Directories](mcp.md#directories).

### Prompt Management

* https://github.com/google/DotPrompt
* https://github.com/elastacloud/DotPrompt #c#
* https://github.com/Akoscianski/dotprompt #python

### Symbolic AI Reasoning (GOFAI?)

<!-- other.ttl has Datalog related research... TODO generate this from that! -->

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
* [Taskfile](https://taskfile.dev)

## Conferences & Events

### Switzerland

* [Linked Data Days](https://www.bfh.ch/de/aktuell/fachveranstaltungen/linked-data-day-2024/) in Bern
* [Knowledge Graph Forum](https://github.com/zazuko/knowledge-graph-forum) in Basel
* [Data Community Conference](https://www.data-community.ch) in Bern

### International

* [Knowledge Graph Conference](https://www.knowledgegraph.tech)
