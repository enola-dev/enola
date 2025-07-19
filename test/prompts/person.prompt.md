---
# Origin: https://github.com/google/dotprompt#example-prompt-file
# TODO Support alt? model: googleai/gemini-1.5-pro
model: google://?model=gemini-2.5-flash
output:
    format: json
    schema:
        # name?: string, the full name of the person
        # age?: number, the age of the person
        # occupation?: string, the person's occupation
        type: object
        properties:
            name:
                type: string
                description: the full name of the person
            age:
                type: number
                description: the age of the person
            occupation:
                type: string
                description: the person's occupation
test:
    text: John Doe is a 35-year-old software engineer living in Zürich.
    expected:
        equals: { "name": "John Doe", "age": 35, "occupation": "software engineer" }
---

Extract the requested information from the given text.
If a piece of information is not present, omit that field from the output.

Text: {{text}}
