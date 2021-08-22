package com.github.bucket1572.springpotato.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.common.InventoryHandler
import com.github.bucket1572.springpotato.common.WandHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent

class SuggestionListGUIListener(private val plugin: SpringPotato): Listener {
    @EventHandler
    fun onReadingSuggestionList(event: PlayerInteractEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!plugin.isRunning) return

        val player = event.player
        val interactionItem = event.item
        val action = event.action

        // 조건
        if (action != Action.RIGHT_CLICK_AIR) return
        if (!WandHandler.isSuggestionListWand(interactionItem)) return

        InventoryHandler.openSuggestionListInventory(player)
    }

    @EventHandler
    fun onInteractingWithSuggestionList(event: InventoryClickEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!plugin.isRunning) return

        // 제안 리스트는 Read-only
        val inventory = event.clickedInventory
        if (inventory?.let { InventoryHandler.isSuggestionListInventory(it) } == true) {
            event.isCancelled = true
        }
    }
}