package io.papermc.paperweight.testplugin

import com.jeff_media.customblockdata.CustomBlockData
import io.papermc.paperweight.testplugin.TickManager.plugin
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Rotation
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Slab
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*


fun setBelt(p:Player,block: Block): Block {
  var direction = p.facing // 플레이어가 바라보는 방향
  if (direction == BlockFace.UP || direction == BlockFace.DOWN) {
    direction = BlockFace.NORTH
  }
  val container = CustomBlockData(block, plugin)
  val blockdata = block.blockData as Slab
  blockdata.type = Slab.Type.TOP
  block.blockData = blockdata
  container.setString(scaffolding_direction, direction.name)
  block.location.clone().add(0.0,1.0,0.0).let { loc->
    val itemFrame = block.world.spawn(loc, ItemFrame::class.java)
    itemFrame.setItem(ItemStack(Material.BIRCH_STAIRS))
    itemFrame.setFacingDirection(org.bukkit.block.BlockFace.UP,true)
    itemFrame.isVisible = false
    itemFrame.itemDropChance = 0f
    itemFrame.rotation = itemFrameFace(direction)

    container.setString("rotation".key,itemFrame.uniqueId.toString())
  }
  return block
}

fun setRotation(block: Block,blockFace: BlockFace) {
    val container = CustomBlockData(block, plugin)
    val uuid = container.get(NamespacedKey("create","rotation"), PersistentDataType.STRING)!!
    block.world.getEntity(UUID.fromString(uuid))?.let { frame->
      (frame as ItemFrame).rotation = when(blockFace){
        BlockFace.WEST-> Rotation.CLOCKWISE_45
        BlockFace.SOUTH-> Rotation.COUNTER_CLOCKWISE_45
        BlockFace.NORTH-> Rotation.CLOCKWISE_135
        BlockFace.EAST-> Rotation.FLIPPED_45
        else-> Rotation.NONE
      }
    }
}

fun itemFrameFace(blockFace: BlockFace) = when(blockFace){
  BlockFace.WEST-> Rotation.CLOCKWISE_45
  BlockFace.SOUTH-> Rotation.COUNTER_CLOCKWISE_45
  BlockFace.NORTH-> Rotation.CLOCKWISE_135
  BlockFace.EAST-> Rotation.FLIPPED_45
  else-> Rotation.NONE
}
