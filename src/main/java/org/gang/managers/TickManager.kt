package org.gang.managers

import com.jeff_media.customblockdata.CustomBlockData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.block.Furnace
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Item
import org.bukkit.entity.ItemDisplay
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import org.gang.TestPlugin
import org.gang.utils.*

object TickManager {
  lateinit var plugin: TestPlugin
  var n = 0

  fun itemEvent() {
    Bukkit.getWorlds().forEach {
      it.entities.filterIsInstance<Item>().forEach { item ->
        item.location.toBlockLocation().clone().subtract(Vector(0, 1, 0)).block.let { block ->
          BlockManager.beltMove(block,item)
        }
      }
    }
  }

//  fun playerEvent() {
//    Bukkit.getOnlinePlayers().forEach { player ->
//      player.location.toBlockLocation().clone().subtract(Vector(0, 1, 0)).block.let { block ->
//        if (hasBlockNearby(player, Material.POLISHED_ANDESITE_STAIRS, 1)) {
//          player.pdc.getString(scaffolding_direction)?.let { direction ->
//            player.velocity = stringToVector(direction).add(Vector(0f,5f,0f)).multiply(0.05).add(player.velocity)
//          }
//        }
//        else if (block.type == Material.POLISHED_ANDESITE_SLAB) {
//          player.pdc.getString(scaffolding_direction)?.let { direction ->
//            player.velocity = stringToVector(direction).multiply(0.05).add(player.velocity)
//          }
//        }
//        else if (block.type == Material.POLISHED_GRANITE_SLAB) {
//          val container: PersistentDataContainer = CustomBlockData(block, plugin)
//          container.getString(scaffolding_direction)?.let { direction ->
//            player.pdc.setString(scaffolding_direction, direction)
//          }
//          player.pdc.getString(scaffolding_direction).let { direction ->
//            player.velocity = stringToVector(direction ?: "").multiply(0.05).add(player.velocity)
//          }
//        }
//      }
//    }
//  }

  fun armorStandEvent() {
    Bukkit.getWorlds().forEach {
      it.entities.filterIsInstance<ItemDisplay>().forEach { stand ->
        if (stand.pdc.has("armor_stand".key)) {

          stand.getNearbyEntities(0.6,0.6,0.6).filterIsInstance<Item>().forEach { item->
            stand.location.clone().subtract(0.5,0.0,0.5).toBlockLocation().block.let { block ->
              if(block.type == Material.CHEST){
                val state = block.state as Chest
                state.inventory.addItem(item.itemStack)
                item.remove()
              }
              else if(block.type == Material.FURNACE){
                val state = block.state as Furnace
                if (item.itemStack.type.isFuel){
                  state.inventory.fuel = item.itemStack
                  item.remove()
                }else{
                  state.inventory.addItem(item.itemStack)
                  item.remove()
                }
              }
            }
          }
          if (n % 20 == 0){
            val loc = stand.location.clone().subtract(0.5,0.0,0.5).toBlockLocation()
            loc.block.let { block ->
              if(block.type == Material.CHEST){
                val state = block.state as Chest
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
                  if (loc.clone().add(direction.first).block.type == Material.IRON_TRAPDOOR) {
                    a++
                    list.add(direction)
                  }
                }
                val n2 = stand.pdc.getOrDefault("n_trapdoor".key, PersistentDataType.INTEGER,0)
                if (a >= 1){
                  state.inventory.first()?.let {
                    val oneItem = it.clone().apply { amount = 1 }  // 1개로 설정된 아이템 복제본

                    loc.world.spawn(stand.location.clone().add(list[n2 % a].first), Item::class.java).apply {
                      this.itemStack = oneItem  // 1개짜리 아이템 스폰
                    }

                    // 원본에서 1개 제거
                    it.amount -= 1
                    if (it.amount <= 0) {
                      state.inventory.remove(it)  // 수량이 0이 되면 인벤토리에서 제거
                    }

                    stand.pdc.set("n_trapdoor".key, PersistentDataType.INTEGER, n2+1)
                  }

                }
              }
              else if(block.type == Material.FURNACE){
                val state = block.state as Furnace
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
                  if (loc.clone().add(direction.first).block.type == Material.IRON_TRAPDOOR) {
                    a++
                    list.add(direction)
                  }
                }
                val n2 = stand.pdc.getOrDefault("n_trapdoor".key, PersistentDataType.INTEGER,0)
                if (a >= 1){
                  state.inventory.result?.let {
                    val oneItem = it.clone()  // 원본 아이템스택을 복제
                    oneItem.amount = 1        // 복제된 아이템의 양을 1개로 설정

                    loc.world.spawn(stand.location.clone().add(list[n2 % a].first), Item::class.java).apply {
                      this.itemStack = oneItem  // 1개로 설정된 아이템을 스폰
                    }

                    // 원본 아이템스택에서 1개 제거
                    it.amount -= 1
                    if (it.amount <= 0) {
                      state.inventory.result = null
                    } else {
                      state.inventory.result = it
                    }

                    stand.pdc.set("n_trapdoor".key, PersistentDataType.INTEGER, n2+1)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
