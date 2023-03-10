# Implementation

## Architecture

The project is _"polyglot"_ (cool with using multiple programming languages).

We follow a _"[UNIX philosophy](https://en.wikipedia.org/wiki/Unix_philosophy)"_-inspired approach to modularity among
the subsystems and within their code.

We do not think it's necessarily all that bad to "shell out" (exec) to invoke existing CLI tools,
if this can significantly accelerate required integrations, or simplify authentication & authorization security.
But such tools such produce output in some machine readable structured text format (such as JSON, YAML, TextProto)
or even a well-known binary format (such as Protocol Buffers binary serialization), not formatted text output intended
for humans.

## Java

The initial implementation of [the Core](core.md) is in Java.

The only reason the initial author (and BDFL) of the project chose Java
was that this allowed him to be most productive, because of his vast prior
knowledge in this particular language ecosystem.

The fact that the core was initially written in Java is very much
considered an "implementation detail" which should not "leak" to
end-users of [the core library](core.md) and its packaging in Editions,
such as for [Kubernetes](k8s/index.md).

GraalVM native image builds are one way to hide Java installlation etc.
