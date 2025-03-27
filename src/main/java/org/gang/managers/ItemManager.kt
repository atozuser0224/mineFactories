@file:Suppress("UnstableApiUsage")

package org.gang.managers

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.core.component.DataComponentType
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.gang.enums.ItemClass
import org.gang.utils.key

object ItemManager {
  val itemList = listOf(
    Material.POLISHED_ANDESITE_SLAB to ItemClass.Fixed,
    Material.POLISHED_GRANITE_SLAB to ItemClass.Rotational,
    Material.POLISHED_ANDESITE_STAIRS to ItemClass.Stairs,
    Material.CHEST to ItemClass.Chest,
    Material.FURNACE to ItemClass.Chest,
    Material.SMOKER to ItemClass.Chest,
  )
  fun getClassByMaterial(material: Material): ItemClass? {
    return (itemList.firstOrNull { it.first == material }?.second)
  }
}

