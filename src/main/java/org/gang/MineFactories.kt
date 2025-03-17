package org.gang

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.checkerframework.checker.nullness.qual.NonNull
import org.checkerframework.framework.qual.DefaultQualifier
import org.gang.events.PlayerEventHandler
import org.gang.managers.TickManager


@DefaultQualifier(NonNull::class)
class TestPlugin : JavaPlugin(), Listener {
  override fun onEnable() {
    this.server.pluginManager.registerEvents(this, this)
    this.server.pluginManager.registerEvents(PlayerEventHandler(this), this)
    TickManager.plugin = this
    launch {
      while (true) {
        delay(1.ticks)
        TickManager.n++
        launch {
          TickManager.itemEvent()
        }
        launch {
          TickManager.armorStandEvent()
        }
      }
    }
  }

}
