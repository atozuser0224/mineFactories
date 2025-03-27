package org.gang.events

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.TrapDoor
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.gang.enums.ItemClass
import org.gang.enums.getNoteBlockType
import org.gang.managers.ItemManager
import org.gang.utils.*
import java.util.*

class PlayerEventHandler(val plugin: JavaPlugin) : Listener {
  @EventHandler
  fun onPlayerJoin(e : PlayerJoinEvent){
    val p = e.player
  }
  @EventHandler
  fun onPlayerQuit(e : PlayerQuitEvent){
  }
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
    else if (e.block.type == Material.IRON_TRAPDOOR) {
      val block = e.block
      val trapdoorData = block.blockData.clone() as? TrapDoor ?: return
      trapdoorData.isOpen = block.getRelative(BlockFace.DOWN).type != Material.CHEST
      block.blockData = trapdoorData
      block.state.update(true, true)  // 강제 업데이트
    }
    else if (e.block.type == Material.BRICK_WALL) {
      val loc = e.block.location.clone().add(0.5,0.0,0.5)
      val stand = loc.world.spawn(loc, ItemDisplay::class.java)

      stand.pdc.set("pump".key, PersistentDataType.BOOLEAN, true)

      e.block.pdc.setString("rotation".key,stand.uniqueId.toString())
    }
    else if (e.block.type == Material.NOTE_BLOCK) {
      val type = getNoteBlockType(e.itemInHand) ?: return
      e.block.blockData = type.getBlockData(e.block)
    }
    ItemManager.itemList.filter { it.second == ItemClass.Chest }.firstOrNull { it.first == e.block.type }?.let {
      val loc = e.block.location.clone().add(0.5,0.0,0.5)
      val stand = loc.world.spawn(loc, ItemDisplay::class.java)
      if (e.block.type == Material.SMOKER) stand.pdc.set("energy".key, PersistentDataType.BOOLEAN, true)
      stand.pdc.set("armor_stand".key, PersistentDataType.BOOLEAN, true)
      e.block.pdc.setString("rotation".key,stand.uniqueId.toString())
    }
  }
  @EventHandler
  fun onInteract(e : PlayerInteractEvent){
    e.clickedBlock?.let { block ->
      block.pdc.get("electro".key, PersistentDataType.INTEGER)?.let { energy->
        e.player.sendMessage("$energy")
      }
    }
  }
  @EventHandler
  fun onBlockBreak(e: BlockBreakEvent) {
    ItemManager.itemList.filter { it.second == ItemClass.Chest }.firstOrNull { it.first == e.block.type }?.let {
      val block = e.block
      if (block.pdc.has("rotation".key)) {
        val uuid = block.pdc.get("rotation".key, PersistentDataType.STRING)!!
        block.world.getEntity(UUID.fromString(uuid))?.remove()
      }
    }
    if (e.block.type == Material.POLISHED_GRANITE_SLAB) {
      val block = e.block
      if (block.pdc.has("rotation".key)) {
        val uuid = block.pdc.get("rotation".key, PersistentDataType.STRING)!!
        block.world.getEntity(UUID.fromString(uuid))?.remove()
      }
    }
    if (e.block.type == Material.BRICK_WALL) {
      val block = e.block
      if (block.pdc.has("rotation".key)) {
        val uuid = block.pdc.get("rotation".key, PersistentDataType.STRING)!!
        block.world.getEntity(UUID.fromString(uuid))?.remove()
      }
    }
  }

}

