import { expect } from "@jest/globals"
import { StringToLongBiMap } from "./string2long"

describe("StringToLongBiMap", () => {
  it("should add and retrieve symbols correctly", () => {
    const map = new StringToLongBiMap()
    const id = map.put("testSymbol")
    expect(id).toBe(0)
    expect(map.getBySymbol("testSymbol")).toBe(id)
    expect(map.getById(id)).toBe("testSymbol")
  })
})
