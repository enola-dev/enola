# Enola 🕵🏾‍♀️

Enola is a tool to help ⛑️ Sysadmins,
[Site Reliability Engineers](https://en.wikipedia.org/wiki/Site_reliability_engineering)
(see [Google's SRE page](https://sre.google)), _"DevOps"_ and developers
investigate the root causes of complex production issues.

Enola has a _model_ of an organization's IT landscape,
offering a _"single pane of glass"_ (SPOG) view of it.
This mimicks the _"picture in the head"_ that knowledgeable senior engineers
typically have of systems, their relations, failure modes, etc. All too often
these are incompletely fully captured by tooling.  Teams often do
have e.g. related documents, [Playbooks](docs/playbook.md), various ad-hoc scripts etc.
This tool can bring them all together, fully integrated. An Org can
do this incrementally over time, improving with each incident
([until 🔮](docs/singularity.md)).
It complements [related existing tools](docs/other.md).

The actual usage of [the underlying core](docs/core.md) is best illustrated by its [Kubernetes Edition](docs/k8s/index.md).
