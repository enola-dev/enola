/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Graph from "graphology"
import { parse } from "graphology-gexf/browser"
import { random } from "graphology-layout"
import forceAtlas2, { inferSettings } from "graphology-layout-forceatlas2"
import { Sigma } from "sigma"

function getElementByIdOrFail(id: string): HTMLElement {
  const element = document.getElementById(id)
  if (!element) {
    throw new Error(`HTML Element with ID '${id}' not found.`)
  }
  return element
}

// TODO Replace hard-coded q=enola:/inline with ?q= read from the URL
fetch("/gexf?q=enola:/inline") // TODO "enola.gexf" for easy Dev mode?! Nah, let's keep it real...
  .then(res => res.text())
  .then(gexf => {
    // Parse GEXF string:
    // TODO Remove addMissingNodes once GexfGenerator adds them itself
    const graph = parse(Graph.DirectedGraph, gexf, { addMissingNodes: true })

    // https://graphology.github.io/standard-library/layout-forceatlas2.html:
    // "Each node’s starting position must be set before running ForceAtlas 2 layout"
    random.assign(graph)

    // Configure ForceAtlas2 layout settings
    // TODO Review and adjust the default settings...
    // TODO Adjust iterations for desired layout quality/performance...
    // https://graphology.github.io/standard-library/layout-forceatlas2.html#settings
    const settings = inferSettings(graph)
    settings.gravity = 1
    // ? settings.scalingRatio = 2
    // ? settings.strongGravityMode = false
    // TODO worker: true, barnesHutOptimize: false, ?
    forceAtlas2(graph, {
      iterations: 500,
      settings,
    })

    // Retrieve some useful DOM elements
    const container = getElementByIdOrFail("container")
    const zoomInBtn = getElementByIdOrFail("zoom-in")
    const zoomOutBtn = getElementByIdOrFail("zoom-out")
    const zoomResetBtn = getElementByIdOrFail("zoom-reset")
    const labelsThresholdRange = getElementByIdOrFail("labels-threshold") as HTMLInputElement

    // Instantiate Sigma.js
    const renderer = new Sigma(graph, container, {
      minCameraRatio: 0.08,
      maxCameraRatio: 3,
    })
    const camera = renderer.getCamera()

    // Bind zoom manipulation buttons
    zoomInBtn.addEventListener("click", () => {
      void camera.animatedZoom({ duration: 600 })
    })
    zoomOutBtn.addEventListener("click", () => {
      void camera.animatedUnzoom({ duration: 600 })
    })
    zoomResetBtn.addEventListener("click", () => {
      void camera.animatedReset({ duration: 600 })
    })

    // Bind labels threshold to range input
    labelsThresholdRange.addEventListener("input", () => {
      renderer.setSetting("labelRenderedSizeThreshold", +labelsThresholdRange.value)
    })

    // Set proper range initial value:
    labelsThresholdRange.value = renderer.getSetting("labelRenderedSizeThreshold").toString()
  })
  .catch(console.error)
