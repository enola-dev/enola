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

Enola is also available to run from a Container image,
using Docker (or Podman, or CRI-O; locally or e.g. on Kubernetes), like this:

    docker run --rm --volume "$PWD":/app/CWD/:Z --tty ghcr.io/enola-dev/enola:main -V

It takes the exact same CLI arguments as the "regular" `enola` binary, but pulls it
via a container image, instead of a "local installation", as above.

The `--volume` argument appropriately mounts the current working directory into the container, so that relative `file:` URIs should work. Absolute paths on your host won't work, because they are not accessible to the container ("by design").

PS: The [`enolac`](../download/latest/enolac) script contains the line above.

## JBang

After [installing JBang](https://www.jbang.dev/download/), please [do](https://github.com/enola-dev/enola/issues/1641):

    jbang catalog add --name enola-dev https://raw.githubusercontent.com/enola-dev/jbang-catalog/main/jbang-catalog.json
    jbang --fresh app install enola@enola-dev

You can now run Enola with:

    enola --help

This runs it from `~/.jbang/bin`, which JBang [added](https://github.com/jbangdev/jbang/issues/2189) to your `PATH`.

To upgrade to the latest version, you need to manually clear the local Maven cache.
The next run of `enola` will then automatically download the latest version:

    rm -rf ~/.m2/repository/dev/enola

This is a workaround, as standard upgrade commands like `jbang --fresh app install --force enola@enola-dev`,
or clearing `~/.jbang/cache`, do not currently work for this application due to its use of `SNAPSHOT`.

## Nix

    nix run --no-sandbox github:enola-dev/enola

If this fails after printing _"warning: ignoring the client-specified setting 'sandbox', because it is a restricted setting and you are not a trusted user",_ then you need to add your username to the `trusted-users` list in `/etc/nix/nix.conf` (e.g., `trusted-users = root your-username-here`), and then restart the Nix daemon with `sudo systemctl restart nix-daemon.service`.

PS: See [issue #1713](https://github.com/enola-dev/enola/issues/1713) re. why `--no-sandbox` is still needed.
