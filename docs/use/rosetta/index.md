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

# Rosetta

Rosetta, inspired by
[the Rosetta Stone](https://en.wikipedia.org/wiki/Rosetta_Stone), transforms
between:

* Text Encodings
* [`YAML`](https://yaml.org) ⇔ [`JSON`](https://www.json.org) ⇔
[`TextProto`](https://protobuf.dev/reference/protobuf/textformat-spec/) ⇔
_[Binary Protocol Buffer "Wire"](https://protobuf.dev/programming-guides/encoding/)_ formats.

Specifying the `--schema` flag is optional for YAML <=> JSON conversion, but required for TextProto.

## YAML to JSON

```bash cd .././.././..
$ ./enola rosetta --in=file:docs/use/library/model.yaml --out=file:docs/use/library/model.json --schema=EntityKinds
...
```

The `model.json` file now contains:

```json
{% include "../library/model.json" %}
```

## YAML to TextProto

```bash cd .././.././..
$ ./enola rosetta --in=file:docs/use/library/model.yaml --out=file:docs/use/library/model.textproto --schema=EntityKinds
...
```

The `model.textproto` file now contains:

```yaml
{% include "../library/model.textproto" %}
```

## YAML to Binary Protocol Buffer

```bash cd .././.././..
$ ./enola rosetta --in=file:docs/use/library/model.yaml --out=file:docs/use/library/model.binpb --schema=EntityKinds
...
```

The `model.binpb` now contains _[binary protocol buffer wire format](https://protobuf.dev/programming-guides/encoding/)._

## UTF-8 to [ISO 8859-1](https://en.wikipedia.org/wiki/ISO/IEC_8859-1) (~ [Windows code page 1252](https://en.wikipedia.org/wiki/Windows-1252))

```bash cd .././.././..
$ file docs/use/rosetta/hello.txt
$ cat docs/use/rosetta/hello.txt
$ ./enola rosetta --in='file:docs/use/rosetta/hello.txt?charset=UTF-8' --out='file:/tmp/hello-windows.txt?charset=ISO-8859-1'
$ file /tmp/hello-windows.txt
$ cat /tmp/hello-windows.txt
...
```
