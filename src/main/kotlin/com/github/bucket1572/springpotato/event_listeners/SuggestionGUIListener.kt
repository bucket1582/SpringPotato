package com.github.bucket1572.springpotato.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.common.InventoryHandler
import com.github.bucket1572.springpotato.common.ScoreHandler
import com.github.bucket1572.springpotato.common.SuggestionHandler
import com.github.bucket1572.springpotato.common.WandHandler
import com.github.bucket1572.springpotato.difficulty_indicator.tag.DifficultyTag
import com.github.bucket1572.springpotato.difficulty_indicator.tag.getIndicator
import com.github.bucket1572.springpotato.difficulty_indicator.tag.getMaterial
import com.github.bucket1572.springpotato.text_components.AlertComponent
import com.github.bucket1572.springpotato.type.ItemType
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class SuggestionGUIListener(private val plugin: SpringPotato): Listener {
    @EventHandler
    fun onBeginningProposal(event: PlayerInteractEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!plugin.isRunning) return

        val player = event.player
        val interactionItem = event.item
        val action = event.action

        if (action == Action.RIGHT_CLICK_AIR && WandHandler.isSuggestionWand(interactionItem)) {
            // 쿨타임이 안 끝났으면 리턴
            if (player.hasCooldown(WandHandler.suggestionWand.material)) {
                player.sendMessage(AlertComponent("아직 쿨타임이 끝나지 않았습니다.").getComponent())
                return
            }
            InventoryHandler.openSuggestionInventory(player)
        }
    }

    @EventHandler
    fun onPropose(event: InventoryClickEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!plugin.isRunning) return

        val inventory = event.inventory

        // 아무것도 안 들고 있으면 끝.
        val item = event.currentItem ?: return
        if (InventoryHandler.isSuggestionInventory(inventory)) {
            if (InventoryHandler.isNotAvailableItem(item)) {
                event.isCancelled = true
                return
            }

            // 플레이어의 경험치 레벨로부터 추가 점수를 계산; 플레이어가 아니면 끝.
            val additionalPoint = ScoreHandler.computeAdditionalScore(event.viewers[0] as Player)

            if (isEasyIndicator(item)) {
                inventory.setItem(7, DifficultyTag.INTERMEDIATE.getIndicator(additionalPoint))
                event.isCancelled = true
                return
            }

            if (isIntermediateIndicator(item)) {
                inventory.setItem(7, DifficultyTag.HARD.getIndicator(additionalPoint))
                event.isCancelled = true
                return
            }

            if (isHardIndicator(item)) {
                inventory.setItem(7, DifficultyTag.EASY.getIndicator(additionalPoint))
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
                DifficultyTag.EASY.getMaterial() -> DifficultyTag.EASY
                DifficultyTag.INTERMEDIATE.getMaterial() -> DifficultyTag.INTERMEDIATE
                DifficultyTag.HARD.getMaterial() -> DifficultyTag.HARD
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

    private fun isEasyIndicator(item: ItemStack): Boolean {
        return item.type == DifficultyTag.EASY.getMaterial() &&
                item.itemMeta.lore() != null &&
                ItemType.DIFFICULTY_INDICATOR.typeComponent.getComponent() in item.itemMeta.lore()!!
    }

    private fun isIntermediateIndicator(item: ItemStack): Boolean {
        return item.type == DifficultyTag.INTERMEDIATE.getMaterial() &&
                item.itemMeta.lore() != null &&
                ItemType.DIFFICULTY_INDICATOR.typeComponent.getComponent() in item.itemMeta.lore()!!
    }

    private fun isHardIndicator(item: ItemStack): Boolean {
        return item.type == DifficultyTag.HARD.getMaterial() &&
                item.itemMeta.lore() != null &&
                ItemType.DIFFICULTY_INDICATOR.typeComponent.getComponent() in item.itemMeta.lore()!!
    }
}