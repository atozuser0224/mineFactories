package io.papermc.paperweight.testplugin

import com.jeff_media.customblockdata.CustomBlockData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.entity.Item
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector

object TickManager {
  lateinit var plugin: TestPlugin
  var n = 0
  fun itemEvent(){
    Bukkit.getWorlds().forEach {
      it.entities.filterIsInstance<Item>().forEach { item ->
        item.location.toBlockLocation().clone().subtract(Vector(0,1,0)).block.let { block->
          if (block.type == Material.POLISHED_ANDESITE_SLAB){
            item.pdc.getString(scaffolding_direction)?.let { direction ->
              item.velocity = stringToVector(direction?:"").multiply(0.05)
            }
          }else if (block.type == Material.POLISHED_GRANITE_SLAB){
            val container: PersistentDataContainer = CustomBlockData(block, plugin)
            if (!item.pdc.has(scaffolding_direction)){

              val centerX = item.location.blockX + 0.5
              val centerZ = item.location.blockZ + 0.5
              item.teleport(item.location.clone().apply {
                x = centerX
                z = centerZ
              })

            }
            if (item.location.x.mod(1.0) in 0.4..0.6 &&
              item.location.z.mod(1.0) in 0.4..0.6
            ){
              container.getString(scaffolding_direction)?.let { direction ->
                item.pdc.setString(scaffolding_direction,direction)
              }
            }
            item.pdc.getString(scaffolding_direction).let { direction ->
              item.velocity = stringToVector(direction?:"").multiply(0.05)
            }
          }
        }
      }
    }
  }

  fun playerEvent(){

  }
}
