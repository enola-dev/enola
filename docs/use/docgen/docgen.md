<!--
    SPDX-License-Identifier: Apache-2.0

    Copyright 2024 The Enola <https://enola.dev> Authors

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

# Models

``` mermaid
classDiagram
  direction RL
  class Book{
    🆔 library
    🆔 isbn
    🆔 copy
  }
  link Book "#demo.book"
  Book -- Library : library
  Book -- Book_kind : kind
  class Book_kind{
    🆔 isbn
    🔗 google
  }
  link Book_kind "#demo.book_kind"
  class Library{
    🆔 id
  }
  link Library "#demo.library"
  class Entity_kind{
    🆔 name
  }
  link Entity_kind "#enola.entity_kind"
  class Schema{
    🆔 fqn
  }
  link Schema "#enola.schema"
```

## 📖 `demo.book` (Book (Copy)) <a name="demo.book"></a>

* library
* isbn
* copy

### Related Entities

* `library` ⇒ [library](#demo.library/{path.library})
* `kind` ⇒ [book_kind](#demo.book_kind/{path.isbn})

## 📗 `demo.book_kind` (Book (Kind)) <a name="demo.book_kind"></a>

* isbn

### Links

* `google` _Google Book Search_ ⇝ <https://www.google.com/search?tbm=bks&q=isbn:{path.isbn}>

## 📚 `demo.library` (Library) <a name="demo.library"></a>

* id

## 🕵🏾‍♀️ `enola.entity_kind` (Enola.dev Entity Kind) <a name="enola.entity_kind"></a>

* name

[See documentation...](https://docs.enola.dev/concepts/core-arch/)

## 💠 `enola.schema` (Schema (Proto) used in Enola Entity Data) <a name="enola.schema"></a>

* fqn

[See documentation...](https://docs.enola.dev/use/connector/#grpc)

---
_This model documentation was generated with ❤️ by [Enola.dev](https://www.enola.dev) @ [76c5d0b9](https://github.com/enola-dev/enola/tree/76c5d0b9)_
