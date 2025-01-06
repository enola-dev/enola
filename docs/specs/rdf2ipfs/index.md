---
"@context": https://enola.dev/spec.jsonld
author: https://www.vorburger.ch
status: Idea
---

# RDF to IPFS

_Author:_ [Michael Vorburger.ch](https://www.vorburger.ch)
<br>_Status:_ **Idea**

## Abstract

We outline and demonstrate how RDF graphs can be store on IPFS with IPLD.

## Description

RDF graphs are DAGs where IRIs identify both nodes and edges.

We propose to consider RDF as a set of _Things_ (triple statements with the same _Subject)._

[Canonicalizing](../../use/canonicalize/index.md) each _Thing_ allows us to create a store them,
and use [their `ipfs:$CID` URL](../../use/fetch/index.md#ipfs) as an alternative IRI,
stored in the RDF store with the original _Thing._

When storing another _Thing,_ we replace the typically `http:` IRI in its links with such `ipfs:$CID`.

There may be a "chicken and egg" problem is you cannot "create a linear sequence" of _Things_ hm...

The serialization format (JSON, CBOR, TTL as Text) does not matter much at least initially - it's the Links that are interesting.

## Implementations

Enola.dev may [further explore this idea](https://github.com/enola-dev/enola/issues/777), depending on our availability.

This Spec would get updated with additional details as we learn more about the feasibility of doing this.

Please reach out if this idea interests you.

## References

* [IPLD](https://ipld.io/)
* [IPFS](https://ipfs.tech/)
* [openEngiadina RDF CBOR Issue #3](https://codeberg.org/openEngiadina/rdf-cbor/issues/3)
