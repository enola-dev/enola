---
"@context": https://enola.dev/spec.jsonld
author: https://www.vorburger.ch
status: Deprecated
---

# Markdown Term

_Author:_ [Michael Vorburger.ch](https://www.vorburger.ch)
<br>_Status:_ **Deprecated**

## Abstract

This _Spec_ defines a _Markdown_ syntax extension to mark up _[Terms](https://en.wikipedia.org/wiki/Term_(language))._

This is useful in order to use Markdown to write knowledge management articles.

It builds upon and extends the [Markdown Magic Links](../markdown-magic-link/index.md) spec.

## Description

Links like `?[[../concepts/xyz.md]]`, note the `?` _question mark_ prefix, are transformed into (something) like:

```html
<a
  href="../concepts/xyz.md"
  title="(A longer description about XYZ to appear on mouse hover over, not just its TITLE)"
  class="some style with dotted underline, and another color than other links">
  XYZ</a>
```

In practice and just for end-user convenience, we expect this to often be used together
either with [Workspace URL References](../url-reference-workspace/index.md) (for example as `?[[¬/xyz]]`),
or the _CURIE_ syntax (for example as `?[[term:xyz]]`, where `term` is configured somewhere as a _prefix_ e.g. for `¬/concepts/`).

## Alternatives

### Mouse Over

Alternatively, a Markdown pre-processor could just always set a `title=` on all links, not just these.

### Automagic

Instead of introducing a special link syntax, if a system "knew" that e.g. `"../concepts/xyz.md"` is a _Term_ it could automagically use another CSS `class` style on links.

One way to declare that could be a `$type: Term` sort of declaration e.g. in [YAML-LD Frontmatter](../markdown-yamlld-frontmatter/index.md).

Such a system could then also generate e.g. an alphabetically sorted _"Glossary"_ using HTML `<dl> <dt> <dd>` markup.

### Marker Character

We considered which "marker" character to use as prefix, and settled on `?` after considering:

* `/` would be confusing both before and inside `[[`
* `!` is used for inline images; e.g. `![Image](http://example.org/image.png)`
* `^` is used for footnotes by at least some Markdown processors
* `@` is often used to "raise" (or "tag") people
* `$` implies "money" (or "value")
* `%` was no better than `?`
* `&` marks an _anchor_ in YAML
* `#` is _anchor_ in URL

## Fallback

One could manually write out such HTML for each _Term_ within each MD, but that seems quite tedious.

## Implementations

Enola.dev's Markdown pre-processor may implement this idea in the future.

## References

As far as we know, this is a novel idea which isn't been used anywhere that we are aware of.
