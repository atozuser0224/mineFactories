package org.gang.events

import org.bukkit.Material
import org.bukkit.block.data.type.Slab
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.plugin.java.JavaPlugin
import org.gang.utils.setBelt

class PlayerEventHandler(val plugin: JavaPlugin) : Listener {

  @EventHandler
  fun onBlockPlace(e: BlockPlaceEvent) {
    if (e.block.type == Material.POLISHED_GRANITE_SLAB) {
      setBelt(e.player, e.block)
    }
    else if (e.block.type == Material.POLISHED_ANDESITE_SLAB) {
      val blockdata = e.block.blockData as Slab
      blockdata.type = Slab.Type.TOP
      e.block.blockData = blockdata
    }
  }
}
