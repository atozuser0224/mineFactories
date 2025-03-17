package org.gang.events

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.data.type.Slab
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.gang.utils.*
import java.util.*

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
    else if (e.block.type == Material.CHEST && e.block.location.clone().subtract(0.0,1.0,0.0).block.type == Material.POLISHED_ANDESITE_SLAB) {
      val loc = e.block.location.clone().add(0.5,0.0,0.5)
      val stand = loc.world.spawn(loc, ArmorStand::class.java)

      stand.pdc.set("armor_stand".key, PersistentDataType.BOOLEAN, true)

      e.block.pdc.setString("rotation".key,stand.uniqueId.toString())
    }
  }

  @EventHandler
  fun onBlockBreak(e: BlockBreakEvent) {
    if (e.block.type == Material.CHEST) {
      val block = e.block
      if (block.pdc.has("rotation".key)) {
        val uuid = block.pdc.get("rotation".key, PersistentDataType.STRING)!!
        block.world.getEntity(UUID.fromString(uuid))?.remove()
      }
    }
  }
}
