package com.github.bucket1572.springpotato.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.common.GameHandler
import com.github.bucket1572.springpotato.common.InventoryHandler
import com.github.bucket1572.springpotato.common.WandHandler
import com.github.bucket1572.springpotato.wands.Wand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent

class WandSettingListener(val plugin: SpringPotato): Listener {
    @EventHandler
    fun onOpeningSettings(event: PlayerInteractEvent) {
        // 게임이 세팅 페이즈가 아니면 무시한다.
        if (!GameHandler.isSettingPhase()) return

        val player = event.player
        val interactionItem = event.item
        val action = event.action

        // 조건
        if (action != Action.RIGHT_CLICK_AIR) return
        if (!WandHandler.isSettingWand(interactionItem)) return

        InventoryHandler.openSettingWandInventory(player)
    }

    @EventHandler
    fun onSetting(event: InventoryClickEvent) {
        // 게임이 진행 중이지 않으면 무시한다.
        if (!GameHandler.isRunning()) return

        val item = event.currentItem ?: return
        val inventory = event.inventory
        val player = inventory.viewers[0] ?: return

        // 조건
        if (!InventoryHandler.isSettingInventory(inventory)) return
        if (InventoryHandler.isNotAvailableItem(item)) {
            event.isCancelled = true
            return
        }
        if (!InventoryHandler.isSettingInventoryOf(inventory, player as Player)) {
            event.isCancelled = true
            return
        }

        WandHandler.addWand(player, WandHandler.getHelperTool(item))
        InventoryHandler.initSettingWandInventory(player)
        event.isCancelled = true
    }
}