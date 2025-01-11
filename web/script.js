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

import { Sigma } from "sigma"
import { parse } from "graphology-gexf/browser"
import graphology from "graphology"

// TODO Replace hard-coded q=enola:/inline with ?q= read from the URL
fetch("/gexf?q=enola:/inline")
  .then(res => res.text())
  .then(gexf => {
    // Parse GEXF string:
    const graph = parse(graphology.Graph, gexf)

    // Retrieve some useful DOM elements:
    const container = document.getElementById("container")
    const zoomInBtn = document.getElementById("zoom-in")
    const zoomOutBtn = document.getElementById("zoom-out")
    const zoomResetBtn = document.getElementById("zoom-reset")
    const labelsThresholdRange = document.getElementById("labels-threshold")

    // Instantiate sigma:
    let renderer = new Sigma(graph, container, {
      minCameraRatio: 0.08,
      maxCameraRatio: 3,
    })
    const camera = renderer.getCamera()

    // Bind zoom manipulation buttons
    zoomInBtn.addEventListener("click", () => {
      camera.animatedZoom({ duration: 600 })
    })
    zoomOutBtn.addEventListener("click", () => {
      camera.animatedUnzoom({ duration: 600 })
    })
    zoomResetBtn.addEventListener("click", () => {
      camera.animatedReset({ duration: 600 })
    })

    // Bind labels threshold to range input
    labelsThresholdRange.addEventListener("input", () => {
      renderer?.setSetting("labelRenderedSizeThreshold", +labelsThresholdRange.value)
    })

    // Set proper range initial value:
    labelsThresholdRange.value = renderer.getSetting("labelRenderedSizeThreshold") + ""
  })
