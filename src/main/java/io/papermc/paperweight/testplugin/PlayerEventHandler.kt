package io.papermc.paperweight.testplugin

import com.jeff_media.customblockdata.CustomBlockData
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Rotation
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Slab
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class PlayerEventHandler(val plugin: JavaPlugin) : Listener {
  @EventHandler
  fun onBlockPlace(e : BlockPlaceEvent){
    if (e.block.type == Material.POLISHED_GRANITE_SLAB){
      setBelt(e.player,e.block)
    }else if (e.block.type == Material.POLISHED_ANDESITE_SLAB) {
      val blockdata = e.block.blockData as Slab
      blockdata.type = Slab.Type.TOP
      e.block.blockData = blockdata
    }
  }
}
