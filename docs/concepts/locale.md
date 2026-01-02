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

# Localization & Internationalization

Enola supports localization (i10n) & internationalization (i18n) out of the box.

## Locale

By default, Enola uses the system's locale:

```sh
$ ./enola test-locale
Now is May 17, 2025, 5:48:06 PM CEST
```

This can be overridden by setting an OS specific mechanism; on Linux with the `LANG` environment variable:

```sh
LANG=de_CH ./enola test-locale
Now is 17. Mai 2025, 17:54:06 MESZ
```

The Enola CLI also supports a `--locale` option (note the use of `-` hyphen instead of `_` underscore):

```sh
$ ./enola --locale=fr-CH test-locale
Now is 17 mai 2025, 17:55:55 CEST
```

## Time Zone

By default, Enola uses the system's TZ, as seen above.

This can be overridden by setting an OS specific mechanism; on Linux with the `TZ` environment variable:

```sh
TZ=America/New_York ./enola test-locale
Now is May 17, 2025, 12:00:18 PM EDT
```

The Enola CLI also supports a `--timeZone` option (note the use of `-` hyphen instead of `_` underscore):

```sh
./enola --timeZone=Europe/Zurich test-locale
Now is May 17, 2025, 6:02:25 PM CEST
```
