package com.github.bucket1572.springpotato.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.common.GameHandler
import com.github.bucket1572.springpotato.common.InventoryHandler
import com.github.bucket1572.springpotato.common.WandHandler
import com.github.bucket1572.springpotato.text_components.AlertComponent
import com.github.bucket1572.springpotato.text_components.SuccessComponent
import com.github.bucket1572.springpotato.wands.Wand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerInteractEvent

class VirtualWandListener(val plugin: SpringPotato) : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        // 제출까지 끝났으면 무시함.
        if (!GameHandler.isHandOutPhase()) return

        val item = event.item ?: return
        val player = event.player

        if (!WandHandler.isVirtual(item)) return

        if (WandHandler.isWand(item, WandHandler.virtualBed)) {
            if (WandHandler.isWandCooldown(player, WandHandler.virtualBed)) {
                player.sendActionBar(AlertComponent("아직 쿨타임이 끝나지 않았습니다.").getComponent())
                return
            }

            player.bedSpawnLocation = player.location
            player.sendActionBar(SuccessComponent("해당 위치를 저장했습니다.").getComponent())
            WandHandler.cooldownWand(player, WandHandler.virtualBed)
            return
        }

        if (WandHandler.isWand(item, WandHandler.virtualChest)) {
            InventoryHandler.openVirtualChest(player)
            return
        }
    }
}