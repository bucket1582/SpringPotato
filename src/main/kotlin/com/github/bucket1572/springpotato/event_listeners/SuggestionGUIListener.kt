package com.github.bucket1572.springpotato.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.common.InventoryHandler
import com.github.bucket1572.springpotato.common.SuggestionHandler
import com.github.bucket1572.springpotato.common.WandHandler
import com.github.bucket1572.springpotato.difficulty_indicator.tag.DifficultyTag
import com.github.bucket1572.springpotato.difficulty_indicator.tag.getIndicator
import com.github.bucket1572.springpotato.text_components.AlertComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent

class SuggestionGUIListener(private val plugin: SpringPotato): Listener {
    @EventHandler
    fun onBeginningProposal(event: PlayerInteractEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!plugin.isRunning) return

        val player = event.player
        val interactionItem = event.item
        val action = event.action

        if (action == Action.RIGHT_CLICK_AIR && WandHandler.isSuggestionWand(interactionItem)) {
            InventoryHandler.openSuggestionInventory(player)
        }
    }

    @EventHandler
    fun onPropose(event: InventoryClickEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!plugin.isRunning) return

        val inventory = event.inventory
        val item = event.currentItem
        if (InventoryHandler.isSuggestionInventory(inventory)) {
            if (InventoryHandler.isNotAvailableItem(item)) {
                event.isCancelled = true
                return
            }

            if (item == DifficultyTag.EASY.getIndicator(0)) {
                inventory.setItem(7, DifficultyTag.INTERMEDIATE.getIndicator(0))
                event.isCancelled = true
                return
            }

            if (item == DifficultyTag.INTERMEDIATE.getIndicator(0)) {
                inventory.setItem(7, DifficultyTag.HARD.getIndicator(0))
                event.isCancelled = true
                return
            }

            if (item == DifficultyTag.HARD.getIndicator(0)) {
                inventory.setItem(7, DifficultyTag.EASY.getIndicator(0))
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onEndingProposal(event: InventoryCloseEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!plugin.isRunning) return

        // Cant use 로 닫힐 경우, 이벤트를 적용하지 않음.
        if (event.reason == InventoryCloseEvent.Reason.CANT_USE) return

        val inventory = event.inventory
        if (InventoryHandler.isSuggestionInventory(inventory)) {
            // 제안한 아이템과 난이도; 없으면 끝.
            val item = inventory.contents[1]?.type ?: return

            // 난이도
            val difficultyIndex = when (inventory.contents[7]?.type) {
                Material.LIME_CONCRETE -> DifficultyTag.EASY
                Material.YELLOW_CONCRETE -> DifficultyTag.INTERMEDIATE
                Material.RED_CONCRETE -> DifficultyTag.HARD
                else -> return
            }

            // 플레이어 받아오기; 플레이어가 아닌 human entity 였다면 끝.
            val player = try{ event.player as Player } catch (e: ClassCastException) { return }

            // 네더의 별일 경우 (불가능)
            // TODO: 2021-08-22 불가능한 대상들을 탐지하기 위한 lore 추가 및 로직 추가.
            if (item == Material.NETHER_STAR) {
                event.player.sendMessage(AlertComponent("네더의 별은 제안할 수 없습니다.").getComponent())
                return
            }

            SuggestionHandler.newSuggestion(plugin, item, player, difficultyIndex)

            player.setCooldown(Material.NETHER_STAR, WandHandler.SUGGESTION_WAND_COOLDOWN)
        }
    }
}