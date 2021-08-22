package com.github.bucket1572.springpotato.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.common.WandHandler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class PlayerDeathListener(val plugin: SpringPotato) : Listener {
    private val INVENTORY_LOSS_PROB = 0.1

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!plugin.isRunning) return

        val killed = event.entity

        // 조건
//        if (killed.killer == null) return

        // 죽으면 1 레벨을 잃음.
        killed.giveExpLevels(-1)

        killed.inventory.forEach {
            if (!WandHandler.isWand(it) && it != null) {
                for (i in 0 until it.amount) {
                    if (Random.nextDouble() < INVENTORY_LOSS_PROB) {
                        it.subtract()
                        val clone = it.clone().asOne()
                        killed.world.dropItem(killed.location, clone)
                    }
                }
            }
        }

        val worldBorder = killed.world.worldBorder
        killed.bedSpawnLocation = worldBorder.center
    }
}