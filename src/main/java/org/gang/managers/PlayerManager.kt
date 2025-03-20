package org.gang.managers

import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.block.data.type.Slab
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.util.RayTraceResult
import org.bukkit.util.Vector

object PlayerManager {
  val displayMap = mutableMapOf<Player, BlockDisplay>()
  fun playerEvent(){
    Bukkit.getOnlinePlayers().forEach { player ->
      val world = player.world
      val rayTrace: RayTraceResult? = world.rayTraceBlocks(
        player.eyeLocation, player.eyeLocation.direction, 4.0, FluidCollisionMode.NEVER, true
      )
      val direction: Vector = player.eyeLocation.direction.multiply(4)
      displayMap[player]?.let{
        it.teleport(rayTrace?.hitPosition?.toLocation(world)?.toBlockLocation()?:(player.eyeLocation.clone().add(direction).toBlockLocation().setRotation(0f,0f)))
        it.block = when(ItemManager.getClassByMaterial(player.inventory.itemInMainHand.type)){
          BeltClass.Fixed -> Material.DIORITE_SLAB.createBlockData().apply {
            (this as Slab).type = Slab.Type.TOP
          }
          BeltClass.Rotational -> Material.DIORITE_SLAB.createBlockData()
          BeltClass.Stairs -> Material.DIORITE_STAIRS.createBlockData()
          null -> Material.AIR.createBlockData()
        }
        if (it.location.block.type != Material.AIR) it.block = Material.AIR.createBlockData()
        it.billboard = Display.Billboard.FIXED
      }
    }
  }
}
