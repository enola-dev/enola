# Kubernetes Edition

Enola 🕵🏾‍♀️ [Kubernetes](https://github.com/vorburger/LearningKubernetes-CodeLabs) Edition
adds support for
the [Kubernetes Resource Model](https://github.com/vorburger/LearningKubernetes-CodeLabs/blob/develop/docs/krm.md) (KRM)
to [Enola Core](../core.md).

Here's how to use it:

1.

Have [access to an installed Kubernetes cluster](https://github.com/vorburger/LearningKubernetes-CodeLabs/blob/develop/docs/install.md)
and have [the `kubectl` CLI](https://github.com/vorburger/LearningKubernetes-CodeLabs/blob/develop/docs/fun/kubecli.md)
working locally, with a valid `~/.kube/config`.

2. [Install Bazelisk](https://github.com/bazelbuild/bazelisk#installation)

3. `git clone` this repo

4. `bazelisk run //...`

<!-- _TODO Run the dogfooding [demo.md](demo.md) executable Playbook, and insert it here, with its captured output!_ -->
