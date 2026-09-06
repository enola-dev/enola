/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 The Enola <https://enola.dev> Authors
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

// Ensure <asciinema-player> displays as a block element
if (!document.getElementById("asciinema-player-style")) {
  const style = document.createElement("style")
  style.id = "asciinema-player-style"
  style.textContent = `
    asciinema-player {
      display: block;
      margin: 1.5em 0;
    }
  `
  document.head.appendChild(style)
}

// Custom web element <asciinema-player src="..."> for Asciinema Player v3
function initAsciinemaPlayer(el) {
  if (el._player || typeof AsciinemaPlayer === "undefined") return
  const src = el.getAttribute("src")
  if (!src) return

  const autoPlayAttr = el.getAttribute("auto-play") || el.getAttribute("autoplay")
  const autoPlay = autoPlayAttr === "false" ? false : true

  const loopAttr = el.getAttribute("loop")
  const loop = loopAttr === "false" ? false : true

  const options = {
    cols: el.getAttribute("cols") ? parseInt(el.getAttribute("cols"), 10) : undefined,
    rows: el.getAttribute("rows") ? parseInt(el.getAttribute("rows"), 10) : undefined,
    autoPlay,
    loop,
    speed: el.getAttribute("speed") ? parseFloat(el.getAttribute("speed")) : undefined,
    idleTimeLimit: el.getAttribute("idle-time-limit") ? parseFloat(el.getAttribute("idle-time-limit")) : 2,
    theme: el.getAttribute("theme") || undefined,
  }

  el._player = AsciinemaPlayer.create(src, el, options)
}

class AsciinemaPlayerElement extends HTMLElement {
  connectedCallback() {
    if (typeof AsciinemaPlayer === "undefined") {
      window.addEventListener("load", () => initAsciinemaPlayer(this))
    } else {
      initAsciinemaPlayer(this)
    }
  }
}

if (!customElements.get("asciinema-player")) {
  customElements.define("asciinema-player", AsciinemaPlayerElement)
}

// Support for Material for MkDocs instant navigation
if (window.document$) {
  document$.subscribe(() => {
    document.querySelectorAll("asciinema-player").forEach(initAsciinemaPlayer)
  })
}
