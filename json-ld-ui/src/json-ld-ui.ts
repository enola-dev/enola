function renderURI(jsonLdURI: string, elementID: string) {
  fetch(jsonLdURI)
    .then(response => response.text())
    .then(textJsonLD => {
      // This isn't for our UI/Form, but "just" so that the fetched JSON LD is "on the page",
      // in order for e.g. https://validator.schema.org or https://search.google.com/test/rich-results
      // et al.to also "see" it!
      const script = document.createElement("script")
      script.setAttribute("type", "application/ld+json")
      script.textContent = textJsonLD
      document.head.appendChild(script)

      // Now we render the UI of the same
      try {
        let jsonLD = JSON.parse(textJsonLD)
        renderObject(jsonLD, elementID)
      } catch (error) {
        console.log("JSON.parse", error)
      }
    })
    .catch(error => console.log("Fetch failed", error))
}

function renderObject(jsonLd: object, elementID: string) {
  document.getElementById(elementID)!.appendChild(document.createElement("div")).innerHTML = JSON.stringify(jsonLd)
}
