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

# Enola.dev 🕵🏾‍♀️

<a class="github-button" href="https://github.com/enola-dev/enola" data-color-scheme="no-preference: light; light: light; dark: dark;" data-icon="octicon-star" data-size="large" data-show-count="true" aria-label="Star enola-dev/enola on GitHub">⭐ Star Us on GitHub! 🫶</a>

_[Click **_Join_** on our Announcements Google Group](https://groups.google.com/g/enoladev-announcements)_ to receive our news & updates!

Enola.dev is an open-source project and community with a three-fold mission:

1. Build a vibrant community for hands-on learning in the rapidly evolving world of AI. (As Feynman famously said: _"What I cannot create, I do not understand.")_
1. [Provide powerful directly useful 🔮 AI-driven 🧙 agents](agents/index.md) that automate your work. These can be used with several AI tools.
1. Offer one such AI Tool; the Enola Application.

This Enola application itself is an **Agentic AI Tool for Everyone:** It enables you to easily create and use [your own _"No Code"_ AI 🥷 agents](tutorial/agents.md). These agents leverage Large Language Models (LLMs) and can interact with external [🧰 Tools](concepts/tool.md) to perform actions, going far beyond simple chat and text generation.

* **Accessible:** It provides both a [CLI and a Web UI](tutorial/chat.md) for interacting with agents.
* **End-User Focused:** It's designed as a "[packaged](use/index.md)" tool that's easy to get started with.
* **Extensible:** While user-friendly, it's also a full-fledged [technical framework](dev/javadoc/index.html) that you can [use as a library](dev/maven.md) to build your own solutions.
* **Forward-Looking:** We are actively exploring advanced concepts like "multi-agent" scenarios.

## Our Vision

We believe the future of AI lies in Knowledge Management Platforms benefiting from the synergy between Large Language Models and structured Knowledge Graphs (KG). Our goal is to further explore the fascinating intersection of these fields, particularly with _AI Graph RAG (Retrieval-Augmented Generation)_. By providing agents with a rich, contextual understanding of _"IT Infrastructure (or other) as Linked Data"_ systems through knowledge graphs, we aim to create more powerful, accurate, and intelligent ⛑️ automation ([until singularity](concepts/singularity.md)).

We may also offer our [built-in Tools for LLMs](concepts/tool.md) as standalone MCP servers for [other AI tools](concepts/other.md#ai-tools) to use in the future.

## Other Tools

Enola includes also a suite of AI unrelated tools:

* **Resource Abstraction:** The [`fetch`](use/fetch/index.md) command can retrieve resources from diverse sources like HTTP(S), Git repositories, and local files.
* **Data Transformation (Rosetta):** The [`rosetta`](use/rosetta/index.md) command converts between numerous data formats (RDF, JSON, YAML, XML, etc.), and can generate diagrams to visualize your models.
* **Executable Markdown:** The [`execmd`](use/execmd/index.md) command allows you to embed executable commands within your Markdown documents, enabling dynamic content generation.
* **Canonicalizing Formatter:** The [`canonicalize`](use/canonicalize/index.md) command helps maintain consistent formatting across your project files.

BTW: The name "Enola" was originally inspired by the project's creator having watched the _Enola Holmes_ 🕵🏾‍♀️ detective movie with his daughter. And this is like a detective's tool!

## See You!

Come join us and help build the future of open AI-powered knowledge and automation together! 🫶 [Chat](chat.md) with us, and [set up your dev env](dev/setup.md).

<script type="application/ld+json">
{% include "models/enola.dev.jsonld" %}
</script>

<script async defer src="https://buttons.github.io/buttons.js"></script>
