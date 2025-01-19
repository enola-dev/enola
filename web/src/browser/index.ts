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

import { DirectedGraph } from "graphology"
import { parse } from "graphology-gexf/browser"
import { random } from "graphology-layout"
import { inferSettings } from "graphology-layout-forceatlas2"
import FA2Layout from "graphology-layout-forceatlas2/worker"
import { Sigma } from "sigma"
import { getElementByIdOrFail } from "./util"

// TODO Handle 404 - display e.g. an "🙅🏽‍♀️" in the DIV container DOM
// TODO Replace hard-coded demo with ?q= read from the URL e.g. for "/gexf?q=enola:/inline"
fetch("/demo/greeting3.gexf")
  .then(res => res.text())
  .then(gexf => {
    // Parse GEXF string:
    // TODO Remove addMissingNodes once GexfGenerator adds them itself
    const graph: DirectedGraph = parse(DirectedGraph, gexf, { addMissingNodes: true })

    // https://graphology.github.io/standard-library/layout-forceatlas2.html:
    // "Each node’s starting position must be set before running ForceAtlas 2 layout."
    // https://www.npmjs.com/package/graphology-layout-forceatlas2#pre-requisites:
    // "(...) edge-case where the layout cannot be computed if all of your nodes starts with x=0 and y=0."
    random.assign(graph)

    // Configure ForceAtlas2 layout settings
    // TODO Review and adjust the default settings...
    // https://graphology.github.io/standard-library/layout-forceatlas2.html#settings
    const fa2Settings = inferSettings(graph)
    fa2Settings.adjustSizes = true // TODO Add Node Sizes to Graph
    // NOT, because unstable: fa2Settings.barnesHutOptimize = true
    fa2Settings.gravity = 1
    fa2Settings.scalingRatio = 1
    fa2Settings.strongGravityMode = true

    const fa2Layout = new FA2Layout(graph, { settings: fa2Settings })
    fa2Layout.start()
    // TODO UI Buttons to stop() and re-start() the layout
    // TODO layout.kill() when element is removed from DOM - but how do we know when to do that?!

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

    const initialLabelsThreshold = 0
    labelsThresholdRange.value = initialLabelsThreshold.toString()
    renderer.setSetting("labelRenderedSizeThreshold", initialLabelsThreshold)
    labelsThresholdRange.addEventListener("input", () => {
      renderer.setSetting("labelRenderedSizeThreshold", +labelsThresholdRange.value)
    })
  })
  .catch((error: Error) => console.error(error))
