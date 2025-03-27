package org.gang.utils

import com.jeff_media.customblockdata.CustomBlockData
import org.bukkit.Location
import org.gang.managers.TickManager.plugin
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Rotation
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Slab
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
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
    itemFrame.isFixed = true
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


fun stringToBlockFace(direction: String): BlockFace {
  return when (direction.uppercase()) {
    "NORTH" -> BlockFace.NORTH
    "SOUTH" -> BlockFace.SOUTH
    "EAST" -> BlockFace.EAST
    "WEST" -> BlockFace.WEST
    "UP" -> BlockFace.UP
    "DOWN" -> BlockFace.DOWN
    else -> BlockFace.SELF // 기본값 설정
  }
}

fun blockFaceToVector(blockFace: BlockFace): Vector {
  return when (blockFace) {
    BlockFace.NORTH -> Vector(0.0, 0.0, -1.0)
    BlockFace.SOUTH -> Vector(0.0, 0.0, 1.0)
    BlockFace.EAST -> Vector(1.0, 0.0, 0.0)
    BlockFace.WEST -> Vector(-1.0, 0.0, 0.0)
    BlockFace.UP -> Vector(0.0, 1.0, 0.0)
    BlockFace.DOWN -> Vector(0.0, -1.0, 0.0)
    else -> Vector(0.0, 0.0, 0.0) // 기본값 (예외 처리)
  }
}
fun noteBlock(){
  val item = ItemStack.of(Material.NOTE_BLOCK)

}
fun stringToVector(direction: String): Vector {
  val blockFace = stringToBlockFace(direction)
  return blockFaceToVector(blockFace)
}

fun hasBlockNearby(entity: Entity, blockType: Material, radius: Int): Boolean {
  val location = entity.location
  for (x in -radius..radius) {
    for (y in -radius..radius) {
      for (z in -radius..radius) {
        val block = location.toBlockLocation().clone().add(x.toDouble(), y.toDouble(), z.toDouble()).block
        if (block.type == blockType && compareVectorsDistance(location.toVector(),block.location.toVector(),1.3)) {
          return true
        }
      }
    }
  }
  return false
}
fun compareVectorsDistance(vec1: Vector, vec2: Vector, threshold: Double): Boolean {
  return vec1.distance(vec2) <= threshold
}
