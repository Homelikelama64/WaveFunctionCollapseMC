package me.squirrelify.bm

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.util.Vector

class TestListener: Listener {
    @EventHandler (priority = EventPriority.LOWEST)
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player
        player.sendMessage("You Jumped")
    }
}
