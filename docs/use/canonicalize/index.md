<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023-2024 The Enola <https://enola.dev> Authors

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

# Canonicalize

_Canonicalization_ transforms a resource into a "standard" representation.

This process is dependent on the type of the resource, but typically includes things such as:

* Formatting based on a fixed standard
* Sorting keys which do not (should not) change semantics
* Rewriting literal values
* Fixed Encoding

This is sometimes useful e.g. when testing, to compare output to a fixed expected outcome.

It also has an application in [cryptography](https://github.com/enola-dev/enola/issues/284), and is useful when _"signing"_ things.

## JSON

`enola canonicalize` for JSON transforms e.g. this `canonicalize.json`:

```json
{% include "../../../test/canonicalize.json" %}
```

into this, using an [RFC 8785](https://www.rfc-editor.org/rfc/rfc8785) _JSON Canonicalization Scheme_ (JCS) -inspired (but currently not fully compliant) algorithm:

```bash cd ../.././..
$ ./enola canonicalize --load=file:test/canonicalize.json
...
```

or more nicely (`--pretty`) formatted:

```bash cd ../.././..
$ ./enola canonicalize --pretty --load=file:test/canonicalize.json
...
```

Note how the order of the keys in the JSON changes, among other changes.
