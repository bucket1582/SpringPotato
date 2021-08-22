package com.github.bucket1572.springpotato.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.common.WandHandler
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class TrackerSettingListener(val plugin: SpringPotato): Listener {
    @EventHandler
    fun onSettingTracker(event: PlayerInteractEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!plugin.isRunning) return

        val player = event.player
        val action = event.action
        val interactionItem = event.item

        // 조건
        if (action != Action.RIGHT_CLICK_AIR) return
        if (!WandHandler.isTrackerWand(interactionItem)) return
        if (player.hasCooldown(WandHandler.trackWand.material)) return

        val randomPlayerExceptUser = plugin.server.onlinePlayers.filter { it != player }.random()

        player.setCooldown(WandHandler.trackWand.material, WandHandler.TRACKER_WAND_COOLDOWN)

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