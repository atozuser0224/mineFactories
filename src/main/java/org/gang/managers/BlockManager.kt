package org.gang.managers

import com.jeff_media.customblockdata.CustomBlockData
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.util.Vector
import org.gang.enums.ItemClass
import org.gang.managers.ItemManager.itemList
import org.gang.managers.TickManager.plugin
import org.gang.utils.*

object BlockManager {
  private const val BELT_SPEED = 0.05
  private val CENTER_RANGE = 0.4..0.6
  private const val DEFAULT_PICKUP_DELAY = 10
  private const val NO_PICKUP_DELAY = Integer.MAX_VALUE

  fun beltMove(block: Block, item: Item) {
    val beltType = itemList.firstOrNull { it.first == block.type } ?: run {
      if (item.pickupDelay > 32765) item.pickupDelay = DEFAULT_PICKUP_DELAY
      return
    }

    when (beltType.second) {
      ItemClass.Stairs -> moveOnStairs(item)
      ItemClass.Rotational -> handleRotationalBelt(block, item)
      else -> moveOnNormalBelt(item)
    }
  }

  private fun moveOnStairs(item: Item) {
    item.pdc.getString(scaffolding_direction)?.let { direction ->
      item.setMovementProperties(
        velocity = stringToVector(direction).add(Vector(0f, 3f, 0f)).multiply(BELT_SPEED)
      )
    }
  }

  private fun moveOnNormalBelt(item: Item) {
    item.pdc.getString(scaffolding_direction)?.let { direction ->
      item.setMovementProperties(
        velocity = stringToVector(direction).multiply(BELT_SPEED)
      )
    }
  }

  private fun handleRotationalBelt(block: Block, item: Item) {
    val container = CustomBlockData(block, plugin)

    if (!item.pdc.has(scaffolding_direction)) {
      centerItem(item)
    }

    if (isItemInCenter(item)) {
      container.getString(scaffolding_direction)?.let { direction ->
        item.pdc.setString(scaffolding_direction, direction)
      }
    }

    moveOnNormalBelt(item)
  }

  private fun centerItem(item: Item) {
    val centerX = item.location.blockX + 0.5
    val centerZ = item.location.blockZ + 0.5
    item.teleport(item.location.clone().apply {
      x = centerX
      z = centerZ
    })
  }

  private fun isItemInCenter(item: Item): Boolean =
    item.location.x.mod(1.0) in CENTER_RANGE &&
      item.location.z.mod(1.0) in CENTER_RANGE

  private fun Item.setMovementProperties(velocity: Vector) {
    this.pickupDelay = NO_PICKUP_DELAY
    this.velocity = velocity
  }
}
