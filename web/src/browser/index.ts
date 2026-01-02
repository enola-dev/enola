/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
fetch("demo/greeting3.gexf")
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

    for (const node of graph.nodeEntries()) {
      // NOK: graph.setNodeAttribute(node, "size", 10)
      node.attributes["size"] = 10
    }

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
    //   incl. renderer.off("upNode", handleUp) ?
    //   incl. renderer.off("upStage", handleUp) ?

    // Retrieve some useful DOM elements
    const container = getElementByIdOrFail("container")
    const zoomInBtn = getElementByIdOrFail("zoom-in")
    const zoomOutBtn = getElementByIdOrFail("zoom-out")
    const zoomResetBtn = getElementByIdOrFail("zoom-reset")
    const labelsThresholdRange = getElementByIdOrFail("labels-threshold") as HTMLInputElement

    // State for drag'n'drop
    let draggedNode: string | null = null
    let isDragging = false

    // Instantiate Sigma.js
    const renderer = new Sigma(graph, container, {
      minCameraRatio: 0.08,
      maxCameraRatio: 3,
    })
    renderer.on("downNode", e => {
      // On mouse down on a node:
      //  - Stop layout-ing (required; otherwise manual and automatic re-positioning "compete")
      //  - Enable the drag mode
      //  - Save in the dragged node in the state
      //  - Highlight the node
      //  - Disable the camera so its state is not updated
      fa2Layout.stop()
      isDragging = true
      draggedNode = e.node
      graph.setNodeAttribute(draggedNode, "highlighted", true)
      if (!renderer.getCustomBBox()) renderer.setCustomBBox(renderer.getBBox())
    })
    renderer.on("moveBody", ({ event }) => {
      // On mouse move, if the drag mode is enabled, we change the position of the draggedNode
      if (!isDragging || !draggedNode) return

      // Get new position of node
      const pos = renderer.viewportToGraph(event)

      graph.setNodeAttribute(draggedNode, "x", pos.x)
      graph.setNodeAttribute(draggedNode, "y", pos.y)

      // Prevent sigma to move camera:
      event.preventSigmaDefault()
      event.original.preventDefault()
      event.original.stopPropagation()
    })
    const handleUp = () => {
      try {
        // On mouse up, we reset the dragging mode
        if (draggedNode) {
          graph.removeNodeAttribute(draggedNode, "highlighted")
        }
      } finally {
        isDragging = false
        draggedNode = null
      }
    }
    renderer.on("upNode", handleUp)
    renderer.on("upStage", handleUp)
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
  .catch((error: unknown) => {
    console.error(error)
  })
