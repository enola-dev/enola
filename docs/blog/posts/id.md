# Simplified ID

Hi,

I've [made good progress](https://github.com/enola-dev/enola/compare/0f2da74...6def72b666f590cecf3efaa5a27ee7a0122b01d0)
over this weekend, with 18 commits with 2,583 additions and 382 deletions since the last update.

The [Core model](https://github.com/enola-dev/enola/blob/6def72b666f590cecf3efaa5a27ee7a0122b01d0/core/lib/src/main/java/dev/enola/core/enola_core.proto)
(and [metamodel](https://github.com/enola-dev/enola/blob/6def72b666f590cecf3efaa5a27ee7a0122b01d0/core/lib/src/main/java/dev/enola/core/meta/enola_meta.proto))
have been further refined, again. The biggest change are probably [several big simplifications of `ID`](https://github.com/enola-dev/enola/compare/0f2da74...6def72b666f590cecf3efaa5a27ee7a0122b01d0#diff-7d89def96bf69e09a6ba609a5dbef7210878e4aa60b538dd4751b5075cf03a3d):

1. I ditched my original `oneof text / parts` idea, because that might have a been a dumb API idea after all? 😼
1. I ditched the `map<string, string> query`, as the keys are already in `EntityKind`, and it can just be order-based. (The only drawback of this is that we cannot have an `Entity` be identified e.g. _either_ by `name` **or** by `uuid`, or whatever, but that may not actually be an issue IRL.)
1. ~~I ditched "composed keys" and, for now, just use a single element. This could be revisited, if a real need for permitting this ever arose.~~ Yeah so no -- that was kept! 🧑‍⚕️

Tx,

M.
