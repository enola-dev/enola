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

Enola.dev is an open-source project with a dual mission: to provide powerful AI-driven agents that automate your work, and to build a vibrant community for hands-on learning in the rapidly evolving world of AI. As Feynman famously said: _"What I cannot create, I do not understand."_

Here's a breakdown of what Enola.dev offers:

* **An Agentic AI Tool for Everyone:** Enola enables you to create and use your own _"No Code"_ AI [agents](../agents/index.md). These agents leverage Large Language Models (LLMs) and can interact with external [Tools](tool.md) to perform actions, going far beyond simple text generation.

    * **Accessible:** It provides both a [CLI and a Web UI](../tutorial/chat.md) for interacting with agents.
    * **End-User Focused:** It's designed as a "[packaged](../use/index.md)" tool that's easy to get started with.
    * **Extensible:** While user-friendly, it's also a full-fledged [technical framework](../dev/javadoc/) that you can [use as a library](../dev/maven.md) to build your own solutions.
    * **Forward-Looking:** We are actively exploring advanced concepts like "multi-agent" scenarios.

* **A Knowledge Management Platform:** Enola has deep roots in Knowledge Graph (RDF/KG) technology, designed to model and document complex systems like IT infrastructure. It captures the "mental model" of experienced engineers by representing systems as interconnected _Entities_ identified by URIs.

* **Powerful Developer Utilities:** Enola includes a suite of tools to streamline your workflows:

    * **Resource Abstraction:** The [`fetch`](../use/fetch/index.md) command can retrieve resources from diverse sources like HTTP(S), Git repositories, and local files.
    * **Data Transformation (Rosetta):** The [`rosetta`](../use/rosetta/index.md) command converts between numerous data formats (RDF, JSON, YAML, XML, etc.) and can generate diagrams to visualize your models.
    * **Executable Markdown:** The [`execmd`](../use/execmd/index.md) command turns your Markdown documents into dynamic, executable reports.
    * **Canonicalizing Formatter:** The [`canonicalize`](../use/canonicalize/index.md) command helps maintain consistent formatting across your project files.

## Our Vision

We believe the future of AI lies in the synergy between Large Language Models and structured Knowledge Graphs. Our goal is to explore the fascinating intersection of these fields, particularly with _AI Graph RAG (Retrieval-Augmented Generation)_. By providing agents with a rich, contextual understanding of your systems through knowledge graphs, we aim to create more powerful, accurate, and intelligent automation.

We may also offer Enola's [built-in Tools for LLMs](tool.md) as standalone MCP servers for other AI tools to use.

Come join us and help build the future of AI-powered knowledge and automation!
