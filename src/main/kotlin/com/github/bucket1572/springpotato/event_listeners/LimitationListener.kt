package com.github.bucket1572.springpotato.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.common.GameHandler
import com.github.bucket1572.springpotato.common.WandHandler
import io.papermc.paper.event.entity.EntityDamageItemEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.meta.Damageable

class LimitationListener(val plugin: SpringPotato) : Listener {
    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent) {
        // 제출까지 끝났다면, 무시한다.
        if (!GameHandler.isHandOutPhase()) return

        val itemUsed = event.item
        if (!WandHandler.isUnBreakable(itemUsed)) return

        event.damage = 0
    }

    @EventHandler
    fun onCraftEvent(event: CraftItemEvent) {
        // 제출까지 끝났으면 무시함.
        if (!GameHandler.isHandOutPhase()) return

        if (
            !event.inventory.any {
                WandHandler.isWand(it)
            }
        ) return

        event.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        // 제출까지 끝났으면 무시함.
        if (!GameHandler.isHandOutPhase()) return

        val item = event.itemInHand
        if (!WandHandler.isVirtual(item)) return

        event.isCancelled = true
    }
}