# Enola 🕵🏾‍♀️

Enola is a tool which helps IT ⛑️ Sysadmins,
[Site Reliability Engineers](https://en.wikipedia.org/wiki/Site_reliability_engineering)
(see [Google's SRE page](https://sre.google)), _"DevOps"_ and service developers to
document their systems and relationships to increase visibility (knowledge management)
and investigate the root causes of complex production issues (troubleshoot)
to operate them reliably with efficiency.

Enola has a _model_ of an organization's IT landscape,
offering a _"single pane of glass"_ (SPOG) view of it.
This mimics the _"picture in the head"_ that knowledgeable senior engineers
typically have of systems, their relations, failure modes, etc. All too often
these are incompletely fully captured by existing mechanisms. Teams often do
have e.g. related documents, [Playbooks](docs/playbook.md), various ad-hoc scripts etc.
This tool can bring them all together, fully integrated. An organization can
do this incrementally over time, improving with each incident
([until 🔮](docs/singularity.md)).
It complements [related existing tools](docs/other.md).

Due to its inherently modular underlying technical framework, its internal data model is highly extensible to a variety
of environments by modeling concepts from private environments and writing bespoke custom API adapters for proprietary
legacy setups.

The actual usage of [the underlying core](docs/core.md) can be illustrated e.g. by
its [Kubernetes Edition](docs/k8s/index.md).
