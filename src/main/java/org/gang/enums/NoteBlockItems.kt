package org.gang.enums

import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.inventory.ItemStack
import org.gang.utils.key

enum class NoteBlockItems(val power : Boolean,val instrument: Instrument,val kor : String) {
  BATTERY(true,instrument = Instrument.XYLOPHONE,"배터리");

  fun getItem(): ItemStack {
    val item = ItemStack(Material.NOTE_BLOCK,1)
    item.itemMeta.persistentDataContainer.set("type".key,org.bukkit.persistence.PersistentDataType.STRING,this.name)
    return item
  }
  fun getBlockData(block: Block): BlockData{
    return (block.blockData as NoteBlock).apply {
      this.instrument = this@NoteBlockItems.instrument
      this.isPowered = this@NoteBlockItems.power
    }
  }
}
fun getNoteBlockType(item: ItemStack): NoteBlockItems? {
  return NoteBlockItems.entries.firstOrNull { it.name == item.itemMeta.persistentDataContainer.get("type".key,org.bukkit.persistence.PersistentDataType.STRING) }
}
