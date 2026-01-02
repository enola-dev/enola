<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2026 The Enola <https://enola.dev> Authors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->

# Demo

This Demo is an [Executable Playbook](../concepts/playbook.md) illustrating the Kubernetes integration,
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

Enola understands all the intrinsic relationships between everything we have illustrated above.
You can therefore do the following, which will do exactly the same as what we just did step by step but instead in one go:

    be available
