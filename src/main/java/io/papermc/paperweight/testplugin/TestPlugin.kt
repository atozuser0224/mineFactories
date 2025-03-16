package io.papermc.paperweight.testplugin

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.jeff_media.customblockdata.CustomBlockData
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.entity.Item
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import org.checkerframework.checker.nullness.qual.NonNull
import org.checkerframework.framework.qual.DefaultQualifier


@DefaultQualifier(NonNull::class)
class TestPlugin : JavaPlugin(), Listener {
    override fun onEnable() {
      this.server.pluginManager.registerEvents(this, this)
      this.server.pluginManager.registerEvents(PlayerEventHandler(this),this)
      TickManager.plugin = this
        launch {
          while (true){
            delay(1.ticks)
            TickManager.n++
            TickManager.itemEvent()
          }
        }
    }

}
fun stringToBlockFace(direction: String): BlockFace {
  return when (direction.uppercase()) {
    "NORTH" -> BlockFace.NORTH
    "SOUTH" -> BlockFace.SOUTH
    "EAST"  -> BlockFace.EAST
    "WEST"  -> BlockFace.WEST
    "UP"    -> BlockFace.UP
    "DOWN"  -> BlockFace.DOWN
    else -> BlockFace.SELF // 기본값 설정
  }
}

fun blockFaceToVector(blockFace: BlockFace): Vector {
  return when (blockFace) {
    BlockFace.NORTH -> Vector(0.0, 0.0, -1.0)
    BlockFace.SOUTH -> Vector(0.0, 0.0, 1.0)
    BlockFace.EAST  -> Vector(1.0, 0.0, 0.0)
    BlockFace.WEST  -> Vector(-1.0, 0.0, 0.0)
    BlockFace.UP    -> Vector(0.0, 1.0, 0.0)
    BlockFace.DOWN  -> Vector(0.0, -1.0, 0.0)
    else -> Vector(0.0, 0.0, 0.0) // 기본값 (예외 처리)
  }
}

fun stringToVector(direction: String): Vector {
  val blockFace = stringToBlockFace(direction)
  return blockFaceToVector(blockFace)
}
