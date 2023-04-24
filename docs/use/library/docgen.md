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
```

## 📖 `demo.book` (Book (Copy)) <a name="demo.book"></a>

* library
* isbn
* copy

### Related Entities

* `library` __ ⇒ [library](#demo.library)
* `kind` __ ⇒ [book_kind](#demo.book_kind)

## 📗 `demo.book_kind` (Book (Kind)) <a name="demo.book_kind"></a>

* isbn

### Links

* `google` _Google Book Search_ ⇝ <https://www.google.com/search?tbm=bks&q={path.isbn}>

## 📚 `demo.library` (Library) <a name="demo.library"></a>

* id

---
_This model documentation was generated with ❤️ by [Enola.dev](https://www.enola.dev)_
