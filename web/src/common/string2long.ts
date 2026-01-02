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

/**
 * Bidirectional map for common "Symbol" String to Long.
 *
 * Inspired by Java dev.enola.common.string2long.StringToLongBiMap.
 */
export class StringToLongBiMap {
  private symbolsMap = new Map<string, number>()
  private symbolsList: string[] = []
  private nextId = 0

  put(symbol: string): number {
    if (this.nextId === Number.MAX_SAFE_INTEGER) {
      throw new Error("Maximum number of symbols reached.")
    }
    const id = this.symbolsMap.get(symbol)
    if (id !== undefined) {
      return id
    } else {
      this.symbolsMap.set(symbol, this.nextId)
      this.symbolsList[this.nextId] = symbol
      return this.nextId++
    }
  }

  getBySymbol(symbol: string): number {
    const id = this.symbolsMap.get(symbol)
    if (id === undefined) {
      throw new Error(`Symbol not found: ${symbol}`)
    }
    return id
  }

  getById(id: number): string {
    if (id >= 0 && id < this.symbolsList.length) {
      return this.symbolsList[id]
    } else {
      throw new Error(`ID not found: ${id.toString()}`)
    }
  }

  size(): number {
    return this.nextId
  }

  symbols(): string[] {
    return this.symbolsList
  }
}
