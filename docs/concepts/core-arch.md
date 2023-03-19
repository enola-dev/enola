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

These _logical models_ are [implemented](implementation.md) and further documented in Protobuf messages.
<!-- TODO Figure out how to add link without breaking markdown-link-check: [Protobuf messages](/dev/proto/core_proto_doc/). -->

## Entity (Model)

As UML-like class diagram (without any implication of OOP class implementation):

``` mermaid
classDiagram
  direction RL
  class ID{
    String scheme
    String entityKind
    String[] segments
  }
  class Entity{
    ID id
    Timestamp ts
    Map~String,String~ href
    Map~String,String~ link
  }
  Entity *-- ID : id
  Entity "1" --> "*" Entity : related
  EntityKind "*" <|-- "1" Entity : IS A
```

Alternatively the same model but as an Entity Relationship (ER) -like diagram,
for those of you finding this notation more familiar (again without
any implication of e.g. any RDBMS persistence implementation):

``` mermaid
erDiagram
  Entity{
    String id
    Timestamp ts
    Map~String,String~ href
    Map~String,String~ link
  }
  Entity ||..o{ Entity : related
  EntityKind ||--|{ Entity : IS-A
```

## Entity Type (Meta Model)

``` mermaid
classDiagram
  class EntityKind{
    string scheme
    string name
    string label
    string emoji
    string logo_url
    string doc_url
  }
  EntityKind *-- Parameter : segments
  EntityKind *-- EntityRelationship : related
  EntityKind *-- WebLink : href
  EntityKind *-- DataLink : link
  class Parameter{
    string ref_id_template
  }
  class EntityRelationship{
    string label
    string description
    string rel_id_template
  }
  class WebLink{
    string label
    string description
    string url_template
  }
  class DataLink{
    string label
    string description
    string uri_template
  }
```

!!! question

    **TODO** Add a `status` to Entity... based on a "state machine"
    (which can visualized as state diagrams) that is TBD in the EntityKind.
