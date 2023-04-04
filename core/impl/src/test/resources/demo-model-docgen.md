# Models

``` mermaid
classDiagram
  direction RL
  class Bar{
    🆔 foo
    🆔 name
    🔗 wiki
    🔗 zzz
  }
  link Bar "#demo.bar"
  Bar -- Foo : foo
  Bar -- Baz : one
  Bar -- Baz : two
  class Baz{
    🆔 uuid
  }
  link Baz "#demo.baz"
  class Foo{
    🆔 name
  }
  link Foo "#demo.foo"
```

## 👩‍🎤 `demo.bar` (FUBAR?) <a name="demo.bar"></a>

* foo
* name

[See documentation...](demo-model.md#bar)

### Related Entities

* `foo` _Foo link_ ⇒ [foo](#demo.foo) (This is the 'parent' Foo.)
* `one` _Primary Baz_ ⇒ [baz](#demo.baz)
* `two` _Secondary Baz_ ⇒ [baz](#demo.baz) (There is always moar to relate to!)

### Links

* `wiki` _Wikipedia_ ⇝ <https://en.wikipedia.org/w/index.php?fulltext=Search&search={name}> (founded by Jimmy Wales)
* `zzz` _Backend_ ⇝ <localhost:50051> (Linked Data in another system)

## `demo.baz` <a name="demo.baz"></a>

* uuid

## 💂 `demo.foo` (Fo-o) <a name="demo.foo"></a>

* name

[See documentation...](demo-model.md#foo)

---
_This model documentation was generated with ❤️ by [Enola.dev](https://www.enola.dev)_
