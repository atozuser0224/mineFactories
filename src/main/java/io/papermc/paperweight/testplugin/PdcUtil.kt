package io.papermc.paperweight.testplugin

import com.jeff_media.customblockdata.CustomBlockData
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

val Block.pdc : CustomBlockData
  get() = CustomBlockData(this,TickManager.plugin)

val Entity.pdc : PersistentDataContainer
  get() = this.persistentDataContainer

fun PersistentDataContainer.getString(key : NamespacedKey) : String?{
  return this.get(key, PersistentDataType.STRING)
}
fun PersistentDataContainer.setString(key : NamespacedKey,value: String){
  this.set(key, PersistentDataType.STRING,value)
}
fun PersistentDataContainer.has(key : NamespacedKey): Boolean{
  return this.has(key, PersistentDataType.STRING)
}
val String.key : NamespacedKey
  get() = NamespacedKey("create",this)
val scaffolding_direction = NamespacedKey("create","scaffolding_direction")
