package com.github.bucket1572.springpotato.suggestion.event_listeners

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.common.difficulty_indicator.tag.DifficultyTag
import com.github.bucket1572.springpotato.common.difficulty_indicator.tag.getIndicator
import com.github.bucket1572.springpotato.common.difficulty_indicator.tag.getMaterial
import com.github.bucket1572.springpotato.basic_logic.handlers.ScoreHandler
import com.github.bucket1572.springpotato.basic_logic.handlers.GameHandler
import com.github.bucket1572.springpotato.inventory.handlers.InventoryHandler
import com.github.bucket1572.springpotato.common.text_components.AlertComponent
import com.github.bucket1572.springpotato.suggestion.handlers.SuggestionHandler
import com.github.bucket1572.springpotato.basic_logic.types.ItemType
import com.github.bucket1572.springpotato.wand.handlers.WandHandler
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class SuggestionGUIListener(private val plugin: SpringPotato) : Listener {
    private val FORCE_CLOSING = 1

    @EventHandler
    fun onBeginningProposal(event: PlayerInteractEvent) {
        // 게임이 제안 페이즈가 아니면 무시한다.
        if (!GameHandler.isSuggestionPhase()) return

        val player = event.player
        val interactionItem = event.item
        val action = event.action

        // 조건
        if (action != Action.RIGHT_CLICK_AIR) return
        if (!WandHandler.isSuggestionWand(interactionItem)) return
        if (WandHandler.isWandCooldown(player, WandHandler.suggestionWand)) {
            player.sendActionBar(AlertComponent("아직 쿨타임이 끝나지 않았습니다.").getComponent())
            return
        }
        if (InventoryHandler.suggestionInventory.viewers.size > 0) {
            player.sendActionBar(AlertComponent("다른 사용자가 제안 중입니다.").getComponent())
            return
        }

        InventoryHandler.openSuggestionInventory(player)

        val forceClosingProposal = Runnable {
            InventoryHandler.suggestionInventory.close()
        }

        Bukkit.getScheduler().runTaskLater(plugin, forceClosingProposal, FORCE_CLOSING.toLong() * 1200)

    }

    @EventHandler
    fun onPropose(event: InventoryClickEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!GameHandler.isRunning()) return

        val inventory = event.inventory

        // 조건
        val item = event.currentItem ?: return
        if (!InventoryHandler.isSuggestionInventory(inventory)) return
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

    @EventHandler
    fun onEndingProposal(event: InventoryCloseEvent) {
        // 플러그인이 로딩 되지 않았거나, 아직 시작하지 않았다면, 무시한다.
        if (!GameHandler.isRunning()) return

        // Cant use 로 닫힐 경우, 이벤트를 적용하지 않음.
        if (event.reason == InventoryCloseEvent.Reason.CANT_USE) return

        val inventory = event.inventory

        // 조건
        if (!InventoryHandler.isSuggestionInventory(inventory)) return

        // 게임이 제안 페이즈가 아니면 제안을 받아들이지 않는다.
        if (!GameHandler.isSuggestionPhase()) {
            inventory.viewers.forEach {
                it.sendActionBar(AlertComponent("제안 시간이 끝났습니다. 제안은 받아들여지지 않습니다.").getComponent())
            }
            return
        }

        // 제안한 아이템과 난이도; 없으면 끝.
        val item = inventory.contents[1] ?: return

        // 난이도
        val difficultyIndex = when (inventory.contents[7]?.type) {
            DifficultyTag.EASY.getMaterial() -> DifficultyTag.EASY
            DifficultyTag.INTERMEDIATE.getMaterial() -> DifficultyTag.INTERMEDIATE
            DifficultyTag.HARD.getMaterial() -> DifficultyTag.HARD
            else -> return
        }

        // 플레이어 받아오기; 플레이어가 아닌 human entity 였다면 끝.
        val player = try {
            event.player as Player
        } catch (e: ClassCastException) {
            return
        }

        // 네더의 별일 경우 (불가능)
        if (WandHandler.isWand(item)) {
            event.player.sendMessage(AlertComponent("도우미 도구들은 제안할 수 없습니다.").getComponent())
            return
        }

        SuggestionHandler.newSuggestion(plugin, item.type, player, difficultyIndex)

        WandHandler.cooldownWand(player, WandHandler.suggestionWand)
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