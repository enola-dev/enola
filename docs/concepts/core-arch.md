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

# Core's Architecture

These _logical models_ are [implemented](implementation.md) and further documented in [Protobuf messages](../dev/proto/core.md).

## Entity (Model)

As an UML-like class diagram:

``` mermaid
classDiagram
  direction RL
  class ID{
    String ns
    String entity
    String[] path
  }
  class Entity{
    Timestamp ts
    Map~String|String~ links
  }
  Entity *-- ID : id
  Entity "1" --> "*" Entity : related
  EntityKind "*" <|-- "1" Entity : IS A
```

<!-- TODO Use Map~String,String~ instead | when https://github.com/mermaid-js/mermaid-live-editor/issues/1223 is fixed? -->

<!--
Alternatively the same model but as an Entity Relationship (ER) -like diagram,
for those of you finding this notation more familiar  (without implying e.g. any RDBMS-based persistence implementation):

``` mermaid
erDiagram
  Entity{
    ID id
    Timestamp ts
    Map~String,String~ links
  }
  Entity ||..o{ Entity : related
  EntityKind ||--|{ Entity : IS-A
```
-->

## Entity Kinds (Meta Model)

``` mermaid
classDiagram
  class ID{
    String ns
    String entity
  }
  class EntityKind{
    string label
    string emoji
    string logo_url
    string doc_url
  }
  EntityKind *-- ID : id
  EntityRelationship *-- ID : id
  EntityKind *-- EntityRelationship : related
  EntityKind *-- Link : link
  class EntityRelationship{
    string label
    string description
  }
  class Link{
    string label
    string description
    string uri_template
  }
```

!!! question

    **TODO** Add a `status` to Entity... based on a "state machine"
    (which can visualized as state diagrams) that is TBD in the EntityKind.
