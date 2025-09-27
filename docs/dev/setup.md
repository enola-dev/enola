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

# Dev Set-Up

## Nix

<!-- https://github.com/vorburger/LearningLinux/blob/develop/nix/docs/install.md -->

[Please install Nix](https://zero-to-nix.com/start/install/) to contribute to this project and run:

* `nix run .#test` to execute unit and integration tests, which should be (relatively) fast

You can also enter the Nix build environment to have the same tools available, which is convenient for development:

    nix develop

Please [start your IDE from within](ide.md) this `nix develop` environment.

We recommend that you also [install `direnv`](https://direnv.net), which conveniently automates running `nix develop` when you `cd`.

!!! warning "Setup is in flux, WIP full Nix adoption!"

    This project is in the process of fully adopting <https://nixos.org> for all required tools. _Much of following is out of date!_

## Scripts

* `./enola` locally builds and then starts Enola; this is equivalent to starting it as an end-user from [the packages](../use/index.md)
* `./test.bash` runs unit and integration tests, and should be (relatively) fast
* `./tools/test-ci/test.bash` runs the same as above and then some slower tests etc. like CI
* `./update.bash` [updates dependencies](dependencies.md)

These scripts assume that Nix is "activated" (`PATH`) in your shell, see above; they currently do not do this by themselves.
So launching them from outside the Nix build environment (AKA `nix develop`) is not supported.

**TODO** _Hashbang magic, if any?_

!!! question "Use Nix apps..."

    These scripts should probably be defined as Nix "apps" and used as `nix run .#test` and `nix run .#enola` ? Contributions welcome!

## Further Reading

You can now read more about:

* [Code Style](style.md)
* [IDE Support](ide.md)
* [Dependencies](dependencies.md)
* [Bazel](bazel.md)

<!-- TODO Review if & how this still works...

## GitHub Codespaces

**We highly recommend you use our ready-made "1 click" [Web/Cloud IDE](ide.md) set-up.**

## Development Environment in a Docker Container

Because it can be a PITA to install all required tools, especially on non-Linux platforms,
this project comes with a containerized ("Docker") development environment, which you can use like this:

1. Get the source code:

        git clone https://github.com/enola-dev/enola.git
        cd enola

1. Build and enter (prompt) the Dev. Env. container, which includes all required tools, but with the source code "local / on host" mounted:

        ./devenv.bash

1. (You're now in the container.) Run the Enola CLI, built from source:

        ./enola

1. (You're still in the container.) Build everything and run the tests:

        ./test.bash

When tests ran fully successfully, then a `.git/hooks/pre-commit` that's useful for development is installed.

-->

## Documentation Writing

To work on documentation, launch:

* `tools/docs/serve-quick.bash` for hot reloading live refresh, which is great while writing (even though it has some limitations)
* `tools/docs/serve-build.bash` for a  "real" (full) docs build, without without the demo "screen cast" recordings (which are slow)
* `tools/docs/serve.bash` for generating the "real" (full) static `site/` exactly as it's deployed on <https://docs.enola.dev>

## Clean Up

Use [Nix](#nix) and do not do `pip install -r requirements.txt` outside of the _virtual environment!_

In case of errors such as `ModuleNotFoundError: No module named 'pre_commit'`, try wiping the cache of https://pre-commit.com:

    rm -rf ~/.cache/pre-commit/

In cases like `ImportError: cannot import name '...' from '...'`, maybe try wiping the user (non-`venv`) Python libs:

    rm -rf ~/.local/lib/python*

For _"cannot parse .renovaterc.json5 because support is missing for json5"_ from `pre-commit run --all-files` you may have to:

    rm -rf ~/.cache/pre-commit/
    pip install -r requirements.txt

But if you are correctly in Nix's development environment (`nix develop`), then there should normally be no need for any this.

## Troubleshooting

### Nix: warning: download buffer is full; consider increasing the 'download-buffer-size' setting (won't work)

Add `download-buffer-size = 524288000` to your `~/.config/nix/nix.conf`.

Remember to `systemctl restart nix-daemon.service`.

See https://github.com/NixOS/nix/issues/11728.

### Nix: warning: ignoring the client-specified setting 'download-buffer-size', because it is a restricted setting and you are not a trusted user

Add `trusted-users = $USER root` to your `/etc/nix/nix.conf` (or `/etc/nix/nix.custom.conf` if your `/etc/nix/nix.conf` has an `!include` for it), replacing `$USER` with your actual username. Remember to `systemctl restart nix-daemon.service`.
