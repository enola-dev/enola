---
"@context": https://enola.dev/spec.jsonld
author: https://www.vorburger.ch
status: Idea
---

# Markdown YAML-LD Codeblocks

_Author:_ [Michael Vorburger.ch](https://www.vorburger.ch)
<br>_Status:_ **Idea**

## Abstract

As an extension of [Markdown YAML-LD Frontmatter](../markdown-yamlld-frontmatter/index.md), we propose that YAML-LD also be extracted from code blocks.

## Description

For example:

<!-- TODO Use includes, but it's broken :=( with MD files which contain ``` -->

```markdown
---
"@context": https://enola.dev/spec.jsonld
$id: https://example.org/spec1
$type: enola:Spec

author: https://www.vorburger.ch
---

# Heading...

Let's define more stuff:

` ` `yaml-ld
author: https://example.org/YOU
` ` `

and also separately:

` ` `yaml-ld
/spec2:
  author: https://example.org/THEY
```

becomes the following RDF, if written as TTL:

```turtle
{% include "./test.ttl" %}
```

## Implementations

Enola.dev's Loader may implement this.

## References

We are not aware of any existing tools permitting mixing Markdown, YAML and RDF this conveniently.
