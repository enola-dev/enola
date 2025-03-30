# list-of-list

[http://example.org/list-of-list](http://example.org/list-of-list)

* [list](http://example.org/list):
    1. [a](http://example.org/a)
    1. [b](http://example.org/b)
    1. [c](http://example.org/c)
* [set](http://example.org/set):
    * [a](http://example.org/a)
    * [b](http://example.org/b)
    * [c](http://example.org/c)

<script type="application/ld+json">
[
    {
        "@id": "http://example.org/list-of-list",
        "http://example.org/set": [
            {
                "@id": "http://example.org/a"
            },
            {
                "@id": "http://example.org/b"
            },
            {
                "@id": "http://example.org/c"
            }
        ],
        "http://example.org/list": [
            {
                "@id": "http://example.org/a"
            },
            {
                "@id": "http://example.org/b"
            },
            {
                "@id": "http://example.org/c"
            }
        ]
    }
]
</script>
