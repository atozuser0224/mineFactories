package org.gang.managers

import com.jeff_media.customblockdata.CustomBlockData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Item
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import org.gang.TestPlugin
import org.gang.utils.*
import kotlin.math.abs

object TickManager {
  lateinit var plugin: TestPlugin
  var n = 0

  fun itemEvent() {
    Bukkit.getWorlds().forEach {
      it.entities.filterIsInstance<Item>().forEach { item ->
        item.location.toBlockLocation().clone().subtract(Vector(0, 1, 0)).block.let { block ->
          if (block.type == Material.POLISHED_ANDESITE_SLAB) {
            item.pdc.getString(scaffolding_direction)?.let { direction ->
              item.velocity = stringToVector(direction).multiply(0.05)
            }
          }
          else if (block.type == Material.POLISHED_GRANITE_SLAB) {
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
              container.getString(scaffolding_direction)?.let { direction ->
                item.pdc.setString(scaffolding_direction, direction)
              }
            }
            item.pdc.getString(scaffolding_direction).let { direction ->
              item.velocity = stringToVector(direction ?: "").multiply(0.05)
            }
          }
        }
      }
    }
  }

  fun playerEvent() {

  }

  fun armorStandEvent() {
    Bukkit.getWorlds().forEach {
      it.entities.filterIsInstance<ArmorStand>().forEach { stand ->
        if (stand.pdc.has("armor_stand".key)) {

          stand.getNearbyEntities(0.6,0.5,0.6).filterIsInstance<Item>().forEach { item->
            stand.location.clone().subtract(0.5,0.0,0.5).toBlockLocation().block.let { block ->
              val state = block.state as Chest
              state.inventory.addItem(item.itemStack)
              item.remove()
            }
          }
          if (n % 10 == 0){
            val loc = stand.location.clone().subtract(0.5,0.0,0.5).toBlockLocation()
            loc.block.let { block ->
              val state = block.state as Chest
              var a = 0
              val list = mutableListOf<Pair<Vector,BlockFace>>()
              val directions = listOf(
                Pair(Vector(1, 0, 0), BlockFace.EAST),    // 오른쪽
                Pair(Vector(-1, 0, 0), BlockFace.WEST),   // 왼쪽
                Pair(Vector(0, 1, 0), BlockFace.UP),      // 위
                Pair(Vector(0, -1, 0), BlockFace.DOWN),   // 아래
                Pair(Vector(0, 0, 1), BlockFace.SOUTH),   // 앞
                Pair(Vector(0, 0, -1), BlockFace.NORTH)   // 뒤
              )
              for (direction in directions) {
                if (loc.clone().add(direction.first).block.type == Material.IRON_TRAPDOOR) {
                  a++
                  list.add(direction)
                }
              }
              val n2 = stand.pdc.getOrDefault("n_trapdoor".key, PersistentDataType.INTEGER,0)
              if (a >= 1){
                state.inventory.first()?.let {
                  loc.world.spawn(loc.clone().add(list[n2 % a].first), Item::class.java).apply {
                    this.itemStack = it
                  }
                  state.inventory.removeItemAnySlot(it)
                  stand.pdc.set("n_trapdoor".key, PersistentDataType.INTEGER,n2+1)
                }
              }
            }
          }
        }
      }
    }
  }
}
