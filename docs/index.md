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

# Enola.dev 🕵🏾‍♀️

Enola is a tool which helps IT ⛑️ Sysadmins,
[Site Reliability Engineers](https://en.wikipedia.org/wiki/Site_reliability_engineering)
(see [Google's SRE page](https://sre.google)), and other _samideanaj adminopsions_ to
document their systems and relationships to increase visibility (knowledge management)
and investigate the root causes of complex production issues (troubleshoot)
to operate them reliably with efficiency.

Enola has a _model_ of an organization's IT landscape,
offering a _"single pane of glass"_ (SPOG) view of it.
This mimics the _"picture in the head"_ that knowledgeable senior engineers
typically have of systems, their relations, failure modes, etc. All too often
these are incompletely fully captured by existing mechanisms. Teams often do
have e.g. related documents, Playbooks, various ad-hoc scripts etc.
This tool can bring them all together, fully integrated. An organization can
do this incrementally over time, improving with each incident
([until 🔮](concepts/singularity.md)).
It complements [related existing tools](concepts/other.md).

We call this _"Infrastructure as Linked Data"_ graphs.

_[Click **_Join_** on our Announcements Google Group](https://groups.google.com/g/enoladev-announcements)!_

<!-- Due to its inherently modular underlying technical framework, its internal data model is highly extensible to a variety
of environments by modeling concepts from private environments and writing bespoke custom API connectors for in-house setups. -->

<!-- TODO The actual usage of [the underlying core](core.md) can be illustrated e.g. by its [Kubernetes Edition](k8s/index.md). -->

<sub>PS: The name "Enola" was originally inspired by the project's creator having watched the _Enola
Holmes_ 🕵🏾‍♀️ detective movie with his daughter, and this is an SRE detective's tool!</sub>
