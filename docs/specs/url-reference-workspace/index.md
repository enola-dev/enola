---
"@context": https://enola.dev/spec.jsonld
author: https://www.vorburger.ch
status: Idea
---

# Workspace Root URL References

_Author:_ [Michael Vorburger.ch](https://www.vorburger.ch)
<br>_Status:_ **Idea**

## Abstract

This _Spec_ introduces a convention for resolving special URLs to the "root of a project's directory" instead of the "physical" file system root.

## Description

A URL resolver supporting this spec will interpret a URL such as `¬/dir/file.txt` to mean _"relative" to the closest parent directory containing an (empty) `.ROOT` **tag file**._

Given a project directory tree looking e.g. like this:

* `/home/user/projects/cool/`
  * `.ROOT` _tag file_ #1
  * `README.md`
  * `docs/`
    * `.ROOT` _tag file_ #2
    * `README.md`
    * `folder/`
      * `index.md`
  * `other/`
    * `whatever.xyz`

The following will be the _Result_ of resolve the _Reference_ given the _Base_:

|Base                 |Reference    |Result          |
|---------------------|-------------|----------------|
|README.md            | README.md   | README.md      |
|README.md            | ¬/README.md | README.md      |
|docs/README.md       | ¬/README.md | docs/README.md |
|docs/folder/index.md | ¬/README.md | docs/README.md |
|other/whatever.xyz   | ¬/README.md | README.md      |

If there is no _tag file_ in any ancestor directory, then an error is raised (instead of it being ignored; as the user likely meant to put one).

Note that the slash after the special character is mandatory; `¬test.txt` is ignored and kept as-is (and thus very likely won't work).

This was, of course, inspired by the `~` convention for `$HOME` which has been prevalent in UNIX shells for many tens of years.

There is, obviously, a small risk that this makes "real" files inside a (relative) directory actually named `¬` in-accessible, but we consider the impact of this drawback as negligible. (The `~` on UNIX could cause similar confusion, but details depend on your shell's escaping rules; here, it's just impossible to use a so named directory from a system with a resolver implementing this Spec.)

This is primarily intended to work on `file:` scheme URIs; but not entirely exclusively, as it could also be made to work to resolve e.g. inside `jar:` (`zip:`?) sort of virtual file systems. It perhaps makes less sense on `http:` just because e.g. web browsers' HTTP clients will not support this spec.

## Alternatives

We initially thought of using `//` instead of `¬`, inspired by [Bazel](https://bazel.build) `WORKSPACE` relative syntax.
However, we suggest that is not such a great idea, because `//` does have a well-defined other meaning; as it's
already the _[protocol-relative URL](https://en.wikipedia.org/wiki/URL#prurl) (PRURL)_ prefix.

One could also consider "just" interpreting absolute `/` references in this way.
But that's again not a great idea - because that really does have a very well defined meaning already;
for a generic solution, you must still be able to reference e.g. `/tmp/file` (or whatever).

Other characters than `¬` which we did consider were `§` and `°` or `|`, but they seemed less clear. The use of `|` (or `|`) specifically, but other than making one think of the (totally unrelated) UNIX _pipe_ concept, was considered to have potential for confusion specifically in the context of [Markdown Magic Links](../markdown-magic-link/index.md) which already use it to separate _URI-Reference_ and _Text_ in their new link syntax.

## Fallback

Without this, one can just write out the full relative path in links.

This essentially just saves having to figure out and type `../../../abc`, and having to maintain that when moving files around.

## Implementations

Enola.dev's I/O sub-system may implement this idea in the future.

## References

* [Gollum](https://github.com/gollum/gollum/wiki#linking-internal-pages) permits absolute paths like `[[/Foo]]`

* MkDocs kind of [supports](https://www.mkdocs.org/user-guide/writing-your-docs/#linking-to-pages) absolute `[text](/another.md)`,
  and then interprets it as _"root of `docs/` directory"_ (instead of "real root of filesystem"). But it recommends against using
  this feature, and [has validation](https://www.mkdocs.org/user-guide/configuration/#validation-of-absolute-links) to detect it.
  (And [mkdocs-abs-rel-plugin](https://github.com/sander76/mkdocs-abs-rel-plugin) did it before it was standard.)
