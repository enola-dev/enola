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

* [`pass`](https://www.passwordstore.org): We recommend using this with GPG on a YubiKey that requires "touch" to decrypt secrets.

Support for other secret managers may be added in the future. Please open an issue if you need a specific one; like:

* Plain File
* [GNOME Keyring](https://github.com/swiesend/secret-service/issues/52)
* [KDE Wallet](https://github.com/purejava/kdewallet)
* Support [`age`](https://github.com/FiloSottile/age) (or [`rage`](https://github.com/str4d/rage)), with [`passage`](https://github.com/FiloSottile/passage); for [TPM](https://github.com/Foxboron/age-plugin-tpm), [SE](https://github.com/remko/age-plugin-se) and [YubiKey](https://github.com/str4d/age-plugin-yubikey)
* macOS Keychain, on Apple's Secure Enclave
* Cloud KMS (various)
* Windows

Which one is used is currently automatically determined. This may be made more configurable in the future.

We will not read _"secrets"_ from environment variables, as this is not secure.
