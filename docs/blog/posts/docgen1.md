---
date: 2023-03-26
authors:
- vorburger
categories:
- announcements
tags:
- model
---

# Markdown Model Documentation Generation v0.1 (2023-03-26)

Ahoy,

I've [made good progress](https://github.com/enola-dev/enola/compare/6def72b666f590cecf3efaa5a27ee7a0122b01d0...5bdcaad3b51eccf195ee258a3bfa8b0d20da5911) over this weekend, with 13 commits in 75 changed files with 2,782 additions and 373 deletions since the last update.

Enola finally has an initial CLI (with the great https://picocli.info!) which actually does something useful, for the first time, so this could be v0.0.1? ;-)

    ./enola --model=file:///home/vorburger/git/github.com/vorburger/enola/core/impl/src/test/resources/demo-model.textproto docgen

This will generate [this Model Documentation in Markdown](https://github.com/enola-dev/enola/blob/5bdcaad3b51eccf195ee258a3bfa8b0d20da5911/core/impl/src/test/resources/demo-model-docgen.md) from this [input in `textproto` format](https://github.com/enola-dev/enola/blob/5bdcaad3b51eccf195ee258a3bfa8b0d20da5911/core/impl/src/test/resources/demo-model.textproto).

The [Core model](https://github.com/enola-dev/enola/blob/5bdcaad3b51eccf195ee258a3bfa8b0d20da5911/core/lib/src/main/java/dev/enola/core/enola_core.proto)
(and [metamodel](https://github.com/enola-dev/enola/blob/5bdcaad3b51eccf195ee258a3bfa8b0d20da5911/core/lib/src/main/java/dev/enola/core/meta/enola_meta.proto))
have been further refined, again. The biggest change are probably [several big simplifications of `ID`](https://github.com/enola-dev/enola/compare/0f2da74...6def72b666f590cecf3efaa5a27ee7a0122b01d0#diff-7d89def96bf69e09a6ba609a5dbef7210878e4aa60b538dd4751b5075cf03a3d):

1. I merged the href and the links, because http: links for human are really just another kind of link
1. I ditched my original `oneof text / parts` idea, because that might have a been a dumb API idea after all? 😼
1. I ditched the `map<string, string> query`, as the keys are already in `EntityKind`, and it can just be order-based. (The only drawback of this is that we cannot have an `Entity` be identified e.g. _either_ by `name` **or** by `uuid`, or whatever, but that may not actually be an issue IRL.)
1. ~~I ditched "composed keys" and, for now, just use a single element. This could be revisited, if a real need for permitting this ever arose.~~ Yeah so no -- that was kept! 🧑‍⚕️

I've also had some fun with:

1. starting [a ProtoBuf validation framework](https://github.com/enola-dev/enola/blob/5bdcaad3b51eccf195ee258a3bfa8b0d20da5911/common/protobuf/src/main/java/dev/enola/common/protobuf/MessageValidators.java) (incomplete, more to come)
1. adding more adding [additional `Resource` implementations](https://github.com/enola-dev/enola/tree/5bdcaad3b51eccf195ee258a3bfa8b0d20da5911/common/common/src/main/java/dev/enola/common/io/resource) (for URIs like `empty:` and `null:` or `error:` and `string:` and `memory:`).

Tx,

M.
