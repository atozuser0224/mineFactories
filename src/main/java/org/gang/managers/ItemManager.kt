package org.gang.managers

import com.jeff_media.customblockdata.CustomBlockData
import org.bukkit.Material
import org.bukkit.Rotation
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.persistence.PersistentDataContainer
import org.gang.managers.TickManager.plugin
import org.gang.utils.*

object ItemManager {
  val beltList = listOf(
    Material.POLISHED_ANDESITE_SLAB to BeltClass.Fixed,
    Material.POLISHED_GRANITE_SLAB to BeltClass.Rotational
  )

  val stairs = listOf(
    Material.POLISHED_ANDESITE_STAIRS to BeltClass.Stairs,
  )

  fun getClassByMaterial(material: Material): BeltClass? {
    return (beltList.firstOrNull { it.first == material }?: stairs.firstOrNull { it.first == material })?.second
  }
}

enum class BeltClass{
  Rotational,
  Fixed,
  Stairs
}
