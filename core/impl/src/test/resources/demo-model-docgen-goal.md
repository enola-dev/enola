<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2023 The Enola <https://enola.dev> Authors

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

# Demo Model

``` mermaid
classDiagram
  direction RL
  class Foo{
    🆔 name
  }
  class Bar{
    🆔 foo
    🆔 name
    🌐 wiki
    🔗 zzz
    drain()
  }
  class Baz{
    🆔 uuid
  }
  Bar *-- Foo : foo
  Bar -- Baz : one
  Bar -- Baz : two
  link Foo "#-foo--fo-o-"
  link Bar "#-bar--not-bard-"
  link Baz "#baz"
```

## 💂 `Foo` (Fo-o)

<!-- **ID:** _`name`_ -->

Foo bla bla.

## 👩‍🎤 `Bar` (not Bard)

<!-- **ID:** [_`foo`_](#💂-foo-fo-o) / _`name`_ -->

Bar bla bla.

### Related Entities

* `one` _Primary Baz_ ⇒ [Baz](#baz)
* `two` _Secondary Baz_ ⇒ [Baz](#baz) (There is always moar to relate to!)

### Hypertext References

* `wiki` _Wikipedia_ ⇝ <https://en.wikipedia.org/w/index.php?fulltext=Search&search={name}> (founded by Jimmy Wales)

### Links

* `zzz` _Backend_ ⇢ `localhost:50051` (Linked Data in another system)

## `Baz`

<!-- **ID:** _`uuid`_ -->

---
_This model documentation was generated with ❤️ by [Enola.dev](https://www.enola.dev)_
