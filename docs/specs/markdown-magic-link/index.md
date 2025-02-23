---
"@context": https://enola.dev/spec.jsonld
author: https://www.vorburger.ch
status: Ready
---

# Markdown Magic Links

_Author:_ [Michael Vorburger.ch](https://www.vorburger.ch)
<br>_Status:_ **Ready to Implement**

## Abstract

This _Spec_ defines an alternative _Markdown_ link syntax which has the following features:

* Link title/label is optional, and is automatic when missing
* Supports [CURIE](https://en.wikipedia.org/wiki/CURIE) (like QName) syntax
* Uses relative, absolute or remote reference (but never a page title)

This is useful in order to use Markdown to write knowledge management articles.

## Description

The `[[URI-Reference|Text]]` syntax causing a _Markdown Pre-Processor_ which implements this _Spec_ to consider that as a link where:

* The text between the double square brackets before the `|` is interpreted as
  an [URI reference](https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#URI_references) to another page; for example:
  When a `first.md` file contains `[[second.md|something else]]` this becomes `[something else](second.md)`.

* The `[[http://example.org/ABC|]]` MediaWiki [Pipe trick](https://www.mediawiki.org/wiki/Help:Links#Pipe_trick) syntax,
  where the part after the "|" is left empty, and "special page name rules" are used, is not supported; it's just ignored,
  so it's the same as `[[http://example.org/ABC]]` without any pipe.

* If there is no `|`, which is how this is typically used, then the link text is automatically obtained from the linked page; for example:
  When a `first.md` file contains `[[second.md]]`, and `second.md` contains `# Badaboum`, then it becomes `[Badaboum](second.md)`.

* The _URI-Reference_ can be any relative "internal" reference; for example: `[[../other/concept.md]]` which will work just as well.

* The _URI-Reference_ cannot be some _"page title"_ (AKA header), only a reference to another filename.

* The _URI-Reference_ may be "broken", and point to a missing file. In that case, the link text is automagically determined.
  It may simply be the full URL, or using some heuristics like e.g. just the last segment - it's up to the implementation.

* The _URI-Reference_ is allowed not to include the file extension; for example: `[[concept]]` is the same as `[[concept.md]]`.

* The _URI-Reference_ supports the [File Root](../url-reference-workspace/index.md) prefix; for example: `[[¬/concept]]`.
  Note that e.g. `[[/ABC]]` (still) means "the MD file named `ABC.md` at the root of the file system of the current computer" - which is most probably **NOT** what you want to link to.

* The _URI-Reference_ can also be a full "external" URL, with a scheme; for example: `[[https://www.vorburger.ch]]`.
  That causes the `TITLE` of the _HTML_ at that URL to be used as the link text.
  This example therefore becomes `[Michael Vorburger's Homepage](https://www.vorburger.ch)`.
  Note that whether that page title is actually fetched, or cached from a previous use, or was otherwise obtained, is an implementation detail.

* The _URI-Reference_ can also use a [CURIE](https://en.wikipedia.org/wiki/CURIE)! The _prefix_ must have been somehow defined by the system.
  It may be something global that's hard-coded in the processor, or (preferable) something configured. When used together
  with [YAML-LD Frontmatter](../markdown-yamlld-frontmatter/index.md), then this example:

  ```markdown
  ---
  "@context":
    wikipedia: https://en.wikipedia.org/wiki/
  ---

  Go learn about [[wikipedia:HTTP]]!
  ```

  will turn into: `Go learn about [HTTP](https://en.wikipedia.org/wiki/HTTP)!`.

* The _URI-Reference_ fully supports _Anchors,_ both "cross-page" (e.g. `[[http://example.org/ABC#anchor_name]]`) as well as "on-page" (e.g. `[[#anchor_name]]`).

* The _URI-Reference_ may be pointing to a page which _"redirects"_ (either via HTTP 301 or 308; or with `<meta http-equiv="Refresh">` in HTML);
  in that case, the "final" page is used. This is useful e.g. to have `[[https://enola.dev/origin]]` turn into `[Origin](https://docs.enola.dev/models/enola.dev/origin/)`.

* The _Markdown Pre-Processor_ may opt to "decorate" the link; for example, it could:
  * Insert an _Emoji_ or _Image_ before the link. How it does this is currently not fully specified here.
        For example: `[[https://www.vorburger.ch]]` it may actually insert a Markdown image tag, obtained e.g. from a [Favicon](https://en.wikipedia.org/wiki/Favicon):
        `![FavIcon](https://www.vorburger.ch/favicon.ico)&nbsp;[Michael Vorburger's Homepage](https://www.vorburger.ch)` (or use an `<img style=...>`).
  * Adorn the link with text or other symbols e.g. about the "status" or "popularity" of the target.

These links are only processed in _"text"_ blocks; incl. e.g. inside Headings, Quotes, Lists & Tables, but not e.g. inside comments, standard links, inline code or code blocks.

## Alternatives

The `[[reference]]` double square brackets syntax was chosen because it visually still _"looks close"_ to Markdown's standard links
(except that the order is reversed, and the URL reference comes first here and the text after).

MediaWiki, which is of course not Markdown based, already uses this syntax. A number of Markdown based tools also use this syntax already.

We briefly considered whether there was any risk of possible confusion with (CommonMark) "standard" Markdown [Link reference definitions](https://spec.commonmark.org/0.31.2/#link-reference-definitions), but as they use single brackets, we don't anticipate this to cause any problems in practice.

Some possible alternatives we considered but opted not to pursue include:

* `<http://example.org/ABC>` ... tempting, as Markdown standard syntax for URL, but:
  * Risk of confusion with current purpose; better not re-define something that has a clear existing meaning.
  * Not obvious syntax how to add link label text; `<http://example.org/ABC|Go there>` looks strange.

* `<<http://example.org/ABC>>` extension of above, but risk of confusion for bad MD processors, and as above.

* `http://example.org/ABC` (simply) is tempting, and some Markdown processor will render
   such "raw" URLs as if they were written as `<http://example.org/ABC>` and render them as hyperlinks.

* `[http://example.org]` with single instead of double square brackets is shorter, but:
  * Is too easy to confuse with "regular text in square brackets" (even with some "heuristics")
  * Could be interpreted as a regular Markdown link with missing label

* `[http://example.org/ABC]()` has been observed to render as <http://example.org/ABC>
  by some Markdown processors (incl. GitHub), but this does not seem to be "guaranteed",
  and in others it may "disappear", which is not great. While technically having the
  exact same number of characters as the proposed syntax, it also "looks more
  confusing" to us (it's as if something was missing).

* `{{ABC}}` could have been an idea, but it already means _"invoke Macro"_ on MediaWiki,
   and is also used by various templating engines, so why cause confusion.

* `((ABC))` just _"looks"_ more like some mathematical expression than a link

* Anything using `^` could be confused by the _Footnotes_ syntax which some Markdown dialects support.

## Testing

The Git repository where the source of this document is hosted contains "test fixtures" for this Spec.

## Implementations

Enola.dev's Markdown pre-processor may implement this idea in the future.

Some other Markdown implementations already support something like this syntax:

* Docusaurus, MkDocs, etc. may (TBC) support this?

## References

* [MediaWiki Link Syntax](https://www.mediawiki.org/wiki/Help:Links)

* [mkdocs-ezlinks-plugin](https://github.com/orbikm/mkdocs-ezlinks-plugin) adds some support to [MkDocs](https://www.mkdocs.org)

* [Obsidian](https://help.obsidian.md/Linking+notes+and+files/Internal+links) supports some of this syntax

* [Quartz](https://quartz.jzhao.xyz/features/wikilinks) has this
