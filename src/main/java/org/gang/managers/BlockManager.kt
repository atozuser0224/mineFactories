package org.gang.managers

import com.jeff_media.customblockdata.CustomBlockData
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.util.Vector
import org.gang.managers.ItemManager.beltList
import org.gang.managers.ItemManager.stairs
import org.gang.managers.TickManager.plugin
import org.gang.utils.*

object BlockManager {
  fun beltMove(block : Block, item: Item){
    beltList.firstOrNull { it.first == block.type }?.let { (material,direction) ->
      item.pdc.getString(scaffolding_direction)?.let { direction2 ->
        item.pickupDelay = Integer.MAX_VALUE  // 아이템 줍기 불가능하게 설정
        item.velocity = stringToVector(direction2).multiply(0.05)
      }
      if (direction == BeltClass.Rotational){
        val container: PersistentDataContainer = CustomBlockData(block, plugin)
        if (!item.pdc.has(scaffolding_direction)) {

          val centerX = item.location.blockX + 0.5
          val centerZ = item.location.blockZ + 0.5
          item.teleport(item.location.clone().apply {
            x = centerX
            z = centerZ
          })

        }
        if (item.location.x.mod(1.0) in 0.4..0.6 && item.location.z.mod(1.0) in 0.4..0.6) {
          container.getString(scaffolding_direction)?.let { direction2 ->
            item.pdc.setString(scaffolding_direction, direction2)
          }
        }
      }
    }?:run {
      stairs.firstOrNull { hasBlockNearby(item, it.first, 1) }?.let { (material, _) ->
        item.pdc.getString(scaffolding_direction)?.let { direction ->
          item.pickupDelay = Integer.MAX_VALUE  // 아이템 줍기 불가능하게 설정
          item.velocity = stringToVector(direction).add(Vector(0f,3f,0f)).multiply(0.05)
        }
      }
    }?:run {
      if (item.pickupDelay > 32765) item.pickupDelay = 10
    }
  }
}
