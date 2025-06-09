<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2025 The Enola <https://enola.dev> Authors

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

1. [`pass`](https://www.passwordstore.org): We recommend using this with GPG on a YubiKey that requires "touch" to decrypt secrets.
1. Insecure unencrypted plain text (YAML) file 😭

Support for other secret managers may be added in the future. Please open an issue if you need a specific one; like:

* [GNOME Keyring](https://github.com/swiesend/secret-service/issues/52)
* [KDE Wallet](https://github.com/purejava/kdewallet)
* Support [`age`](https://github.com/FiloSottile/age) (or [`rage`](https://github.com/str4d/rage)), with [`passage`](https://github.com/FiloSottile/passage); for [TPM](https://github.com/Foxboron/age-plugin-tpm), [SE](https://github.com/remko/age-plugin-se) and [YubiKey](https://github.com/str4d/age-plugin-yubikey)
* macOS Keychain, on Apple's Secure Enclave
* Cloud KMS (various)
* Windows

Which one is used is currently automatically determined. This may be made more configurable in the future.

We will not read _"secrets"_ from environment variables, as this is not secure.

## Tests

Because Bazel changes `$HOME`, the integration tests running under Bazel (`BAZEL_TEST`) will read secrets from the file to which the `ENOLA.DEV_AZKABAN` environment variable points. If it's not set, then no secrets are available. We test for the presence of secrets, and skip tests if the required is not available. Launch such integration tests like this, as the `test.bash` script also does:

    bazelisk test --test_env=ENOLA.DEV_AZKABAN="$HOME/keys.yaml" //java/dev/enola/common/secret/auto:tests

To run integration tests [in an IDE](../../dev/ide.md), add this environment variable in the Bazel Test launch config.
