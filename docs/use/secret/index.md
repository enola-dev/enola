<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2025-2026 The Enola <https://enola.dev> Authors

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

# Secrets

Enola manages _"secrets"_ (e.g. API keys, other tokens, passwords, etc.) by delegating to an external secret manager.
The following ones are currently supported:

## `pass`

We recommend using [`pass`](https://www.passwordstore.org) with GPG, ideally on a YubiKey that requires "touch" to decrypt secrets.

Enola will store all of its secrets as YAML inside the pass secret named `enola.dev`, i.e. in `~/.password-store/enola.dev.gpg`.

This secret manager is used when `pass` is available on the `PATH` **and** `$HOME/.password-store/enola.dev.gpg` exists.
On first use, you have to manually enable it once e.g. using `pass edit enola.dev` with a first secret (in YAML syntax).

## YAML

An insecure unencrypted plain text (YAML) file 😭 is used if `pass` is not installed and available on the `PATH`.

This file is must be in an OS specific configuration direction; on Linux it's `~/.config/enola/azkaban.yaml`.

## JVM Properties

If the secret is not found in the aforementioned managers, then JVM properties (AKA `java -D...`) are checked.

## Environment Variables

If the secret is not found anywhere else, then Environment Variables are checked as a last resort.

When setting Environment Variables to "secret" values, be aware that all child processes can see them.

## Other

Support for other secret managers may be added in the future. Please open an issue if you need a specific one; like:

* [GNOME Keyring](https://github.com/swiesend/secret-service/issues/52)
* [KDE Wallet](https://github.com/purejava/kdewallet)
* Support [`age`](https://github.com/FiloSottile/age) (or [`rage`](https://github.com/str4d/rage)), with [`passage`](https://github.com/FiloSottile/passage); for [TPM](https://github.com/Foxboron/age-plugin-tpm), [SE](https://github.com/remko/age-plugin-se) and [YubiKey](https://github.com/str4d/age-plugin-yubikey)
* macOS Keychain, on Apple's Secure Enclave
* Cloud KMS (various)
* Windows

Which one is used is currently automatically determined. This may be made more configurable in the future.

## Tests

Because Bazel changes `$HOME`, the integration tests running under Bazel (`BAZEL_TEST`) will read secrets from the file to which the `ENOLA.DEV_AZKABAN` environment variable points. Launch such integration tests like this, as the `test.bash` script also does:

    bazelisk test --test_env=ENOLA.DEV_AZKABAN=/home/YOUR-UID/.config/enola/azkaban.yaml //java/dev/enola/common/secret/auto:tests

Note that Bazel [will reduce the visible environment variables](https://bazel.build/reference/test-encyclopedia),
so if you quote `$HOME` it would be expanded "too late". It's simplest to just use the full path to your home directory.

To run integration tests [in an IDE](../../dev/ide.md), add this environment variable in the Bazel Test launch config.
(Alternatively, when manually launching individual tests, you also just set a JVM property or environment variable,
because it will ultimately fall back to those secret managers, even when running tests.)

In configuration UIs of IDEs such as IntelliJ, be careful NOT to accidentally include quotes!

PS: Note that this mechanism (erm, work-around) means that Bazel won't be aware of changes made to this "external" file,
so it will not re-run tests using new or updated secrets if (only) the content of `azkaban.yaml` change. _TODO: Find a
smarter way by somehow making the external secret file part of Bazel's build dependencies..._

PPS: We test for the presence of secrets and skip tests if the required secret is not available.
