package org.gang.managers

import org.bukkit.*
import org.bukkit.block.*
import org.bukkit.block.data.Directional
import org.bukkit.block.data.Waterlogged
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import org.gang.TestPlugin
import org.gang.enums.WaterType
import org.gang.utils.*

object TickManager {
  lateinit var plugin: TestPlugin
  var n = 0

  fun itemEvent() {
    Bukkit.getWorlds().forEach { world ->
      world.entities.filterIsInstance<Item>().forEach { item ->
        val blockBelow = item.location.toBlockLocation()
          .clone()
          .subtract(Vector(0, 1, 0))
          .block
        BlockManager.beltMove(blockBelow, item)
      }
    }
  }

  private fun handleWaterEntity(stand: ItemDisplay) {
    stand.incrementTick()
    if (shouldRemoveEntity(stand)) {
      stand.remove()
      return
    }

    spawnWaterParticle(stand)

    when (stand.location.block.type) {
      Material.WAXED_COPPER_BLOCK -> handleCopperBlock(stand)
      Material.BRICK_WALL -> handleBrickWall(stand)
      Material.LIGHTNING_ROD -> handleLightningRod(stand)
      else -> stand.remove()
    }
  }

  private fun ItemDisplay.incrementTick() {
    val currentTick = this.pdc.getOrDefault("tick".key, PersistentDataType.INTEGER, 0)
    this.pdc.set("tick".key, PersistentDataType.INTEGER, currentTick + 1)
  }

  private fun shouldRemoveEntity(stand: ItemDisplay): Boolean {
    return stand.pdc.getOrDefault("tick".key, PersistentDataType.INTEGER, 0) >= 20 * 10
  }

  private fun spawnWaterParticle(stand: ItemDisplay) {
    if (n % 5 == 0) {
      val waterType = WaterType.entries.firstOrNull {
        stand.pdc.get("type".key, PersistentDataType.STRING) == it.name
      }
      val color = waterType?.color ?: Color.BLUE
      val dustOptions = Particle.DustOptions(color, 1.0f)

      stand.location.world.spawnParticle(
        Particle.DUST,
        stand.location.clone().add(0.0, 0.5, 0.0),
        5,
        0.1, 0.1, 0.1,
        0.0,
        dustOptions
      )
    }
  }

  private fun handleInventoryInteraction(stand: ItemDisplay, block: Block) {
    when (val state = block.state) {
      is Chest -> handleChestInteraction(state, stand)
      is Furnace -> handleFurnaceInteraction(state, stand)
      is Smoker -> handleSmokerInteraction(state, stand)
    }
  }

  private fun handlePump(stand: ItemDisplay) {
    if (n % 60 != 0) return

    val data = stand.location.block.blockData as? Waterlogged ?: return
    if (!data.isWaterlogged) return

    val loc = stand.location
    val item = loc.world.spawn(loc, ItemDisplay::class.java).apply {
      pdc.set("type".key, PersistentDataType.STRING, WaterType.WATER.name)
      pdc.set("water".key, PersistentDataType.BOOLEAN, true)
    }

    data.isWaterlogged = false
    loc.block.blockData = data
  }

  fun armorStandEvent() {
    Bukkit.getWorlds().forEach { world ->
      world.entities.filterIsInstance<ItemDisplay>().forEach { stand ->
        when {
          stand.pdc.has("water".key) -> handleWaterEntity(stand)
          stand.pdc.has("pump".key) -> handlePump(stand)
          stand.pdc.has("armor_stand".key) -> {
            val block = stand.location.block
            if (block.type in listOf(Material.CHEST, Material.FURNACE, Material.SMOKER)) {
              handleInventoryInteraction(stand, block)
            }
          }
        }
      }
    }
  }
  private fun handleCopperBlock(stand: ItemDisplay) {
    if (n % 20 != 0) return

    val directions = ChestManager.setDirection(stand.location, Material.LIGHTNING_ROD)
    val vectors = directions.first.filter {
      it.second == (stand.location.clone().add(it.first).block.blockData as Directional).facing
    }

    if (vectors.isEmpty()) return

    val n2 = stand.pdc.getOrDefault("n_trapdoor".key, PersistentDataType.INTEGER, 0)
    stand.teleport(stand.location.clone().add(vectors[n2 % vectors.size].first))
    stand.pdc.set("tick".key, PersistentDataType.INTEGER, 0)
    stand.pdc.set("n_trapdoor".key, PersistentDataType.INTEGER, n2 + 1)
  }

  private fun handleBrickWall(stand: ItemDisplay) {
    stand.teleport(stand.location.clone().add(blockFaceToVector(BlockFace.UP).multiply(0.3)))
  }

  private fun handleLightningRod(stand: ItemDisplay) {
    val data = stand.location.block.blockData as Directional
    stand.teleport(stand.location.clone().add(blockFaceToVector(data.facing).multiply(0.3)))
  }

  private fun handleChestInteraction(state: Chest, stand: ItemDisplay) {
    // 아이템 수집 처리
    collectNearbyItems(stand, state)

    // 아이템 배출 처리
    if (n % 20 == 0) {
      distributeItems(state, stand)
    }
  }

  private fun handleFurnaceInteraction(state: Furnace, stand: ItemDisplay) {
    // 아이템 수집 처리
    collectNearbyItems(stand, state)

    // 결과물 배출 처리
    if (n % 20 == 0) {
      distributeResults(state, stand)
    }
  }

  private fun handleSmokerInteraction(state: Smoker, stand: ItemDisplay) {
    // 아이템 수집 처리
    collectNearbyItems(stand, state)

    // 에너지 생성 처리
    if (n % 20 == 0) {
      createEnergy(state, stand)
    }
  }

  private fun collectNearbyItems(stand: ItemDisplay, container: BlockState) {
    stand.getNearbyEntities(0.6, 0.6, 0.6)
      .filterIsInstance<Item>()
      .forEach { item ->
        when (container) {
          is Chest -> {
            container.inventory.addItem(item.itemStack)
            item.remove()
          }
          is Furnace -> handleFurnaceItems(container, item)
          is Smoker -> handleSmokerItems(container, item)
        }
      }
  }

  private fun handleFurnaceItems(furnace: Furnace, item: Item) {
    val itemStack = item.itemStack
    if (itemStack.type.isFuel) {
      when {
        furnace.inventory.fuel == null -> {
          furnace.inventory.fuel = itemStack
          item.remove()
        }
        furnace.inventory.fuel?.type == itemStack.type -> {
          furnace.inventory.fuel = furnace.inventory.fuel?.apply {
            amount += itemStack.amount
          }
          item.remove()
        }
      }
    } else {
      furnace.inventory.addItem(itemStack)
      item.remove()
    }
  }

  private fun handleSmokerItems(smoker: Smoker, item: Item) {
    val itemStack = item.itemStack
    if (itemStack.type.isFuel) {
      when {
        smoker.inventory.fuel == null -> {
          smoker.inventory.fuel = itemStack
          item.remove()
        }
        smoker.inventory.fuel?.type == itemStack.type -> {
          smoker.inventory.fuel = smoker.inventory.fuel?.apply {
            amount += itemStack.amount
          }
          item.remove()
        }
      }
    }
  }

  private fun distributeItems(chest: Chest, stand: ItemDisplay) {
    val loc = stand.location.clone().subtract(0.5, 0.0, 0.5).toBlockLocation()
    val directions = ChestManager.setDirection(loc, Material.IRON_TRAPDOOR)
    val list = directions.first
    val directionCount = directions.second

    if (directionCount < 1) return

    val n2 = stand.pdc.getOrDefault("n_trapdoor".key, PersistentDataType.INTEGER, 0)
    chest.inventory.first()?.let { item ->
      val oneItem = item.clone().apply { amount = 1 }

      loc.world.spawn(stand.location.clone().add(list[n2 % directionCount].first), Item::class.java) {
        it.itemStack = oneItem
      }

      item.amount -= 1
      if (item.amount <= 0) {
        chest.inventory.remove(item)
      }

      stand.pdc.set("n_trapdoor".key, PersistentDataType.INTEGER, n2 + 1)
    }
  }

  private fun distributeResults(furnace: Furnace, stand: ItemDisplay) {
    val loc = stand.location.clone().subtract(0.5, 0.0, 0.5).toBlockLocation()
    val directions = ChestManager.setDirection(loc, Material.IRON_TRAPDOOR)
    val list = directions.first
    val directionCount = directions.second

    if (directionCount < 1) return

    furnace.inventory.result?.let { result ->
      val n2 = stand.pdc.getOrDefault("n_trapdoor".key, PersistentDataType.INTEGER, 0)
      val oneItem = result.clone().apply { amount = 1 }

      loc.world.spawn(stand.location.clone().add(list[n2 % directionCount].first), Item::class.java) {
        it.itemStack = oneItem
      }

      result.amount -= 1
      if (result.amount <= 0) {
        furnace.inventory.remove(result)
      }

      stand.pdc.set("n_trapdoor".key, PersistentDataType.INTEGER, n2 + 1)
    }
  }

  private fun createEnergy(smoker: Smoker, stand: ItemDisplay) {
    val directions = ChestManager.setDirection(stand.location, Material.LIGHTNING_ROD)
    val vectors = directions.first.filter {
      it.second == (stand.location.clone().add(it.first).block.blockData as Directional).facing
    }

    if (vectors.isEmpty()) return

    smoker.inventory.result?.let { result ->
      val n2 = stand.pdc.getOrDefault("n_trapdoor".key, PersistentDataType.INTEGER, 0)

      stand.location.world.spawn(
        stand.location.clone().add(vectors[n2 % vectors.size].first),
        ItemDisplay::class.java
      ).apply {
        pdc.set("type".key, PersistentDataType.STRING, WaterType.ENERGY.name)
        pdc.set("water".key, PersistentDataType.BOOLEAN, true)
      }

      result.amount -= 1
      if (result.amount <= 0) {
        smoker.inventory.remove(result)
      }

      stand.pdc.set("n_trapdoor".key, PersistentDataType.INTEGER, n2 + 1)
    }
  }

}
