---
date: 2023-03-19
authors:
  - vorburger
categories:
  - announcements
tags:
  - model
---

# First Model (2023-03-19)

Hello,

I've [made good progress](https://github.com/enola-dev/enola/compare/0f2da74...6def72b666f590cecf3efaa5a27ee7a0122b01d0) over this weekend, with 18 commits on   with 2,583 additions and 382 deletions since the last update.

The [Core model](https://github.com/enola-dev/enola/blob/6def72b666f590cecf3efaa5a27ee7a0122b01d0/core/lib/src/main/java/dev/enola/core/enola_core.proto) (and [metamodel](https://github.com/enola-dev/enola/blob/6def72b666f590cecf3efaa5a27ee7a0122b01d0/core/lib/src/main/java/dev/enola/core/meta/enola_meta.proto)) look better now, and there are [some very early first Entity demo models for illustration here](https://github.com/enola-dev/enola/tree/6def72b666f590cecf3efaa5a27ee7a0122b01d0/connectors/demo/src/main/resources/dev/enola/demo).

[Mermaid](https://mermaid.js.org) is very cool to [make diagrams like these](https://github.com/enola-dev/enola/blob/6def72b666f590cecf3efaa5a27ee7a0122b01d0/docs/concepts/core-arch.md), which are perhaps easier to communicate than a .proto? 😏 - but unfortunately you cannot see them yet, due to some rendering issue on GitHub. (I'm hoping to publish an actual documentation site later, where you'll be able to see the diagrams.)

I've also had some fun coding [a cool Resource API](https://github.com/enola-dev/enola/tree/6def72b666f590cecf3efaa5a27ee7a0122b01d0/common/common/src/main/java/dev/enola/common/io/resource) used e.g. to [read & write protobuf in different formats](https://github.com/enola-dev/enola/blob/6def72b666f590cecf3efaa5a27ee7a0122b01d0/common/protobuf/src/main/java/dev/enola/common/protobuf/ProtoIO.java).

Tx,

M.
