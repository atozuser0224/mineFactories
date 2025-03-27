package org.gang.managers

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.LightningRod
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

object ChestManager {
  fun setDirection(loc : Location,material: Material): Pair<MutableList<Pair<Vector, BlockFace>>, Int> {
    var a = 0
    val list = mutableListOf<Pair<Vector,BlockFace>>()
    val directions = listOf(
      Pair(Vector(1, 0, 0), BlockFace.EAST),    // 오른쪽
      Pair(Vector(-1, 0, 0), BlockFace.WEST),   // 왼쪽
      Pair(Vector(0, 1, 0), BlockFace.UP),      // 위
      Pair(Vector(0, 0, 1), BlockFace.SOUTH),   // 앞
      Pair(Vector(0, 0, -1), BlockFace.NORTH)   // 뒤
    )
    for (direction in directions) {
      if (loc.clone().add(direction.first).block.type == material) {
        a++
        list.add(direction)
      }
    }
    return list to a
  }

  fun setDirectionElectric(loc : Location,material: Material): Pair<MutableList<Pair<Vector, BlockFace>>, Int> {
    var a = 0
    val list = mutableListOf<Pair<Vector,BlockFace>>()
    val directions = listOf(
      Pair(Vector(1, 0, 0), BlockFace.EAST),    // 오른쪽
      Pair(Vector(-1, 0, 0), BlockFace.WEST),   // 왼쪽
      Pair(Vector(0, 1, 0), BlockFace.UP),      // 위
      Pair(Vector(0, 0, 1), BlockFace.SOUTH),   // 앞
      Pair(Vector(0, 0, -1), BlockFace.NORTH)   // 뒤
    )
    for (direction in directions) {
      val targetBlock = loc.clone().add(direction.first).block
      if (targetBlock.type == material) {
        // 안전한 타입 체크 추가
        val blockData = targetBlock.blockData
        if (blockData is LightningRod && blockData.facing == direction.second) {
          a++
          list.add(direction)
        }
      }
    }

    return list to a
  }

}
