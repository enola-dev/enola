/**
 * Bidirectional map for common "Symbol" String to Long.
 *
 * Inspired by Java dev.enola.common.string2long.StringToLongBiMap.
 */
export class StringToLongBiMap {
  private symbolsMap: Map<string, number> = new Map()
  private symbolsList: string[] = []
  private nextId: number = 0

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
      throw new Error(`ID not found: ${id}`)
    }
  }

  size(): number {
    return this.nextId
  }

  symbols(): string[] {
    return this.symbolsList
  }
}
