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

# What is Enola.dev?

Enola.dev is a couple of things:

* **Agents:** To help YOU automate your tasks & work using AI; see [agents](../agents/index.md).

* **Community:** Enola is an open place to learn hands-on and together about the rapidly evolving fascinating world of all things AI related. Feynman famously said: _"What I cannot create, I do not understand."_

* **An Agentic AI Tool:** It allows you to create and use your own _"No Code"_ AI agents for various tasks. These agents leverage Large Language Models (LLMs) and can interact with external [Tools](tool.md) to perform actions, going beyond simple text generation. Check out the [agents tutorial](../tutorial/agents.md) to get started. This aspect makes it similar to related [coding frameworks](other.md#agents-frameworks) and similar [tools](other.md#ai-tools). Some differences include that:

    * It [exposes both](../tutorial/chat.md) a CLI and a Web UI.
    * We are actively exploring "multi agent" scenarios.
    * It's positioned primarily as a "[packaged](../use/index.md)" end-user tool.
    * It's internally also [a technical framework](../dev/javadoc/) (and you could [use it as such too](../dev/maven.md)).
    * We "control" our code and can evolve it as needed in the future.

* **A Knowledge Management Platform:** Enola started out as more of a Knowledge Graph (RDF/KG) project, to help you model and document complex systems, particularly IT infrastructure. It aims to capture the "mental model" that experienced engineers have of systems, their relationships, and failure modes. We call this approach _Infrastructure as Linked Data_. It uses concepts like _Entities_, identified by URIs, to build a rich, interconnected models. We have a strong interest in further exploring the fascinating intersection of such knowledge graphs with _AI Graph RAG,_ to provide agents with a rich context and understanding of IT systems models.

* **MCP <!--(+A2A?)--> Servers:** We may in the future make Enola's own [built-in Tools for LLMs](tool.md) available as standalone MCP servers for use by other AI tools.

* **Utilities**

    * **Resource Abstraction:** The [`fetch`](../use/fetch/index.md) command can retrieve resources using various schemes, including HTTP(S), Git repositories, local files.

    * **Data Transformation Tool (Rosetta):** The [`rosetta`](../use/rosetta/index.md) command can convert between numerous data formats, including RDF Turtle, JSON, YAML, XML, and Protocol Buffers. It can also generate graph diagrams from your models, helping you visualize complex relationships.

    * **Executable Markdown:** The [`execmd`](../use/execmd/index.md) command allows you to embed executable commands within Markdown documents, enabling dynamic content generation.

    * **Canonicalizing Formatter:** The [`canonicalize`](../use/canonicalize/index.md) command helps you maintain consistent formatting files, ensuring readability and standardization. It's also useful for certain testing scenarios.

Come join us!
