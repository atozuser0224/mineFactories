package org.gang.enums

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.gang.utils.key

enum class ItemClass{
  Rotational,
  Fixed,
  Stairs,
  Chest
}

enum class Items(val item : ItemStack){
  ENERGY(ItemStack(Material.PAPER,1).apply {
    this.itemMeta.persistentDataContainer.set("energy".key, org.bukkit.persistence.PersistentDataType.INTEGER,1)
    this.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("energy"))
    this.setData(DataComponentTypes.CUSTOM_NAME, net.kyori.adventure.text.Component.text("에너지").color(NamedTextColor.GREEN))
  })
}

operator fun ItemStack.plus(name: String): ItemStack {
  return this.apply {
    // Adventure API를 사용하여 이름 설정
    this.setData(DataComponentTypes.CUSTOM_NAME, net.kyori.adventure.text.Component.text(name))
  }
}
