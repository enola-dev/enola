<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2025 The Enola <https://enola.dev> Authors

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

# Using Enola.dev

<!--The following chapters demonstrate different usage scenarios with recorded demos.-->

This page documents several options available to locally install and run Enola.

## TGZ x86_64

For Linux, download the [`enola.x86_64.tgz`](../download/latest/enola.x86_64.tgz)
archive, then extract it, e.g. with `tar xvf enola.x86_64.tgz`, then run it with
`enola/bin/enola`.

This distribution includes an appropriate Java Runtime Environment (JRE).

## JAR

If you have `java` (>=21) on your `$PATH`, then you can download
[`enola.jar`](../download/latest/enola.jar) and launch it using
`java -jar enola.jar`.

## Downloader

[`enola-dl`](../download/latest/enola-dl) downloads the latest version (into
`~/.cache/`) when required, and then directly runs it. If you put this somewhere on
your `$PATH`, e.g. into your `~/bin/`, then you will automagically always be running
the latest up-to-date version of Enola. (We recommend actually saving it renamed as
`enola` instead of `enola-dl`, just for convenience of launching.)

As is currently implemented (using basic `curl`), there is a bit of a start-up time
overhead for this. Future enhancements may further optimize this; e.g. check only
once a day or so.

Of course, whether you are comfortable with such _"Continuous Delivery",_ and thus
_"always living at `HEAD`",_ like in _"rolling release distros",_ or have any concerns
with such an approach e.g. from a security perspective, is entirely your choice - YMMV.

## Container

[`enolac`](../download/latest/enolac) runs Enola from a Container, on Docker (or Podman,
or CRI-O; locally or e.g. on Kubernetes).

It takes the exact same CLI arguments as the "regular" `enola` binary, but pulls it
via a container image, instead of a "local installation", as above.

It appropriately "mounts" the current working directory into the container, so that
relative `file:` URIs should work. Absolute paths on your host won't work, because they
are not accessible to the container ("by design").

## JBang

From [our Maven repo](../dev/maven.md), using [JBang](https://www.jbang.dev/):

    jbang --repos https://docs.enola.dev/maven-repo/,mavencentral,jitpack --main=dev.enola.cli.EnolaApplication dev.enola:enola:0.0.1-SNAPSHOT

<!-- TODO Make this work... it doesn't quite, yet:

    jbang --repos https://docs.enola.dev/maven-repo/,mavencentral,jitpack --main=dev.enola.cli.EnolaApplication dev.enola:enola:0.0.1-SNAPSHOT server --chatPort=7070 --lm="google://?model=gemini-2.5-flash" --http-scheme --agents=https://raw.githubusercontent.com/enola-dev/enola/refs/heads/main/test/agents/chef-optimist.agent.yam
-->
