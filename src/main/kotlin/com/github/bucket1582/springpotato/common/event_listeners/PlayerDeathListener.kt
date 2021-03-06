package com.github.bucket1582.springpotato.common.event_listeners

import com.github.bucket1582.springpotato.SpringPotato
import com.github.bucket1582.springpotato.basic_logic.handlers.GameHandler
import com.github.bucket1582.springpotato.wand.handlers.WandHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import kotlin.random.Random

class PlayerDeathListener(val plugin: SpringPotato) : Listener {
    private val inventoryLossProb = 0.1

    private val expLvTransfer = 2

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        // 게임이 제출 가능 페이즈가 아니면 무시한다.
        if (!GameHandler.isHandOutPhase()) return

        val killed = event.entity

        // 죽으면 레벨을 잃음.
        killed.giveExpLevels(-expLvTransfer)

        val killer = killed.killer ?: return

        killed.inventory.forEach {
            if (!WandHandler.isWand(it) && it != null) {
                for (i in 0 until it.amount) {
                    if (Random.nextDouble() < inventoryLossProb) {
                        it.subtract()
                        val clone = it.clone().asOne()
                        killed.world.dropItem(killed.location, clone)
                    }
                }
            }
        }

        // 리스폰 킬 방지
        killed.bedSpawnLocation = null

        WandHandler.removeCooldown(killer, WandHandler.trackWand)
        killer.giveExpLevels(expLvTransfer)
    }
}