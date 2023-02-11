# Core

Enola 🕵🏾‍♀️ Core is a library which [implements](implementation.md) generic concepts of Enola's problem space.

It is independant of "domains" such as Network, Linux, Kubernetes, Web, etc.

End-users use Enola through different "editions". Organization can build their own
internal editions of Enola, to interface with their proprietary in-house systems.

This Core's functionality is exposed through different Tools. The focus of the initial work is the `be` CLI tool, as illustrated by the [Kubernetes Edition](k8s/index.md).
