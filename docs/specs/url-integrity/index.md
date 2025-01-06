---
"@context": https://enola.dev/spec.jsonld
author: https://www.vorburger.ch
status: Review
---

# URL Integrity

_Author:_ [Michael Vorburger.ch](https://www.vorburger.ch)
<br>_Status:_ **In public review,** _feedback welcome!_

## Abstract

In order to guarantee the integrity of web (HTML, JS, CSS, etc.) resources fetched by HTTP, we propose a "convention" to add an `?integrity=` URL query parameter. This is then used to verify its value as a Message Authentication Code (MAC) against the byte stream of the resource.

## Description

The value of the `?integrity=` URL query parameter is a Multiformats' [Multibase](https://github.com/multiformats/multibase) encoded [Multihash](https://www.multiformats.io/multihash/).

The verification happens after having (fully) fetched a resource by HTTP, but before consumers use it.

This can be done by an HTTP client, or post-processor, such as:

* CLI downloading tool
* Servers-side library used to fetch remote content by HTTP
* Web Browser Extension / Plug-In
* Web Browser HTTP stack natively
* Client-side JavaScript? While a Client-side JavaScript library loaded over HTTP by an HTML page could check the integrity of additional resources it loads, there is a "chicken and egg" problem - because you cannot trust that the first load wasn't already tampered with. Therefore, the other approaches must be used.

## Implementations

This is currently implemented as described by [the Enola.dev `fetch`](../../use/fetch/index.md#integrity) CLI command; see [announcement](https://groups.google.com/g/enoladev-announcements/c/hANIJgvmGVE).

## Alternatives

HTTPS should also guarantee the integrity between web server and browser. But it has a different "trust model" than this approach, relying on trustworthy certificate authorities, and there are some known challenges with that.

[IPFS](https://ipfs.tech) also solves this, differently again. As far as the author understands, the integrity guarantee only really holds when installing and using local Nodes, and browser extensions which redirect `/ipfs/` HTTP requests to them, but cannot be enforced when using remote IPFS HTTP Gateways, which you then implicitly must trust again.

It's of course also possible to "manually" do the equivalent by using [`sha256sum`](https://man7.org/linux/man-pages/man1/sha256sum.1.html)-like CLI commands, but such explicit steps have much lower usability than something directly integrated into clients.

## #TODO

1. Explore hacking a browser extension which validates `integrity=` on page load
1. Write a HTML post-processor (like [KISSfp](https://www.vorburger.ch/kissfp/) was!) which adds it to all a/href on local files of a static site
1. Consider if using `integrity` could cause confusion e.g. with HTML SRI, as syntax is different?
1. Consider risk of "clashes" with other query parameters so named

## References

* HTTP Signatures: [Internet Draft](https://datatracker.ietf.org/doc/html/draft-cavage-http-signatures-12), from the [W3C Credentials Community Group](https://github.com/w3c-ccg/http-signatures)
* HTML's Subresource Integrity (SRI) `script integrity`: See [Mozilla](https://developer..org/en-US/docs/Web/Security/Subresource_Integrity), [Spec](https://html.spec.whatwg.org/multipage/scripting.html#attr-script-integrity), [w3schools](https://www..com/tags/att_script_integrity.asp)
* [Bazel’s `http_archive`](https://bazel.build/rules/lib/repo/http) rule `integrity` and `sha256` attributes
* [Trusty URIs](https://trustyuri.net/) (also [on GitHub](https://github.com/trustyuri))
* [Peergos' Secret Links](https://book.peergos.org/features/secret.html)
