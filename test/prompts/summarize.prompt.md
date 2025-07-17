---
# Origin: https://google.github.io/dotprompt/getting-started/
#
# model: google://?model=gemini-2.5-flash
input:
    schema:
        # TODO Support this Picoschema (or change it to JSON Schema)
        text: string
output:
    format: json
    schema:
        # title?: string, the title of the article if it has one
        # summary: string, a 3-sentence summary of the text
        # tags?(array, a list of string tag category for the text): string,
        type: object
        properties:
            title: { type: string }
            summary: { type: string }
            tags:
                type: array
                items: { type: string }
        required: [ summary ]
---

Extract the requested information from the given text. If a piece of information is not present, omit that field from the output.

Text: {{text}}
