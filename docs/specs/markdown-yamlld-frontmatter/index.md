---
"@context": https://enola.dev/spec.jsonld
author: https://www.vorburger.ch
status: Review
---

# Markdown YAML-LD Frontmatter

_Author:_ [Michael Vorburger.ch](https://www.vorburger.ch)
<br>_Status:_ **In public review,** _feedback welcome!_

## Abstract

We propose that RDF loaders, in addition to processing formats such as [`*.ttl` Turtle 🐢](https://docs.enola.dev/concepts/turtle/) etc.
also load `*.md` Markdown files, and simply consider any YAML in their _Frontmatter_ as [YAML-LD](https://json-ld.github.io/yaml-ld/spec/).

## Description

For example:

```markdown
{% include "./test.md" %}
```

becomes the following RDF, if written as TTL:

```turtle
{% include "./test.ttl" %}
```

If there is no explicit `"@id"` in the YAML of the MD, then the RDF IRI of the Thing is the MD resource URL.

## Fallback

One could write separate `thing.md` for documentation and `thing.yamlld` for RDF, but it seems nice to be able to keep this together in 1 single file.

## Implementations

Enola.dev's Loader is likely going to implement this.

## References

While a number of tools support YAML front matter, we are not aware of any directly supporting RDF.
