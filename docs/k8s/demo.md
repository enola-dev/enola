# Demo

This Demo is an [Executable Playbook](../playbook.md) illustrating the Kubernetes integration,
using the `be` CLI tool's `TARGET-STATE ENTITY ID` syntax.

1. Start Enola's Be server daemon process:

       bed &

1. Make sure you have access to a running Kubernetes cluster:

       be available k8s/cluster version=1.26.1 ctx=enola-demo

1. ...

       be ...

1. ...

       be available

1. You can now stop our demo cluster like this:

       be stopped k8s/cluster ctx=enola-demo

The fun thing is that Enola understands all the intrinsic relationships between everything we have illustrated above.
You can therefore do the following, which will do exactly the same as what we just did step by step but instead in one
go,
based on the [index.be.textproto](index.be.textproto):

    be available
