package com.github.bucket1572.springpotato.wand.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.basic_logic.handlers.GameHandler
import com.github.bucket1572.springpotato.wand.handlers.WandHandler
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class TrackerSettingListener(val plugin: SpringPotato): Listener {
    @EventHandler
    fun onSettingTracker(event: PlayerInteractEvent) {
        // 게임이 제출 페이즈가 아니면 무시한다.
        if (!GameHandler.isHandOutPhase()) return

        val player = event.player
        val action = event.action
        val interactionItem = event.item

        // 조건
        if (action != Action.RIGHT_CLICK_AIR) return
        if (!WandHandler.isTrackerWand(interactionItem)) return
        if (WandHandler.isWandCooldown(player, WandHandler.trackWand)) return
        if (plugin.server.onlinePlayers.size <= 1) return

        val randomPlayerExceptUser = plugin.server.onlinePlayers.filter { it != player }.random()

        WandHandler.cooldownWand(player, WandHandler.trackWand)

        val trackTarget = Runnable {
            player.compassTarget = randomPlayerExceptUser.location
        }

        val taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, trackTarget, 0L, 20L)

        val endTracking = Runnable {
            Bukkit.getScheduler().cancelTask(taskId.taskId)
        }

        Bukkit.getScheduler().runTaskLater(plugin, endTracking, WandHandler.TRACKER_WAND_COOLDOWN.toLong())
    }
}