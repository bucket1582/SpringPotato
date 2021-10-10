package com.github.bucket1572.springpotato.inventory.handlers

import com.github.bucket1572.springpotato.basic_logic.handlers.ScoreHandler
import com.github.bucket1572.springpotato.suggestion.handlers.SuggestionHandler
import com.github.bucket1572.springpotato.wand.handlers.WandHandler
import com.github.bucket1572.springpotato.common.difficulty_indicator.tag.DifficultyTag
import com.github.bucket1572.springpotato.common.difficulty_indicator.tag.getIndicator
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object InventoryHandler {
    private const val COLUMNS = 9

    private val nullItem = ItemStack(
        Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1
    )

    // 제안 창
    val suggestionInventory = Bukkit.createInventory(
        null, InventoryType.DISPENSER, Component.text("제안")
    )

    // 제안 목록 창
    private val suggestionListInventory = Bukkit.createInventory(
        null, COLUMNS * 3, Component.text("제안 목록")
    )

    // 도구 설정 창 (플레이어에 종속)
    private val settingWandInventoryMap = mutableMapOf<Player, Inventory>()

    // 개인 상자 창 (플레이어에 종속)
    private val virtualChestInventoryMap = mutableMapOf<Player, Inventory>()

    init {
        nullItem.editMeta {
            it.displayName(Component.text(" "))
        }
    }

    fun isSuggestionInventory(inventory: Inventory): Boolean {
        return inventory == suggestionInventory
    }

    fun isSuggestionListInventory(inventory: Inventory): Boolean {
        return inventory == suggestionListInventory
    }

    fun isSettingInventory(inventory: Inventory): Boolean {
        return inventory in settingWandInventoryMap.values
    }

    fun isSettingInventoryOf(inventory: Inventory, player: Player): Boolean {
        return inventory == settingWandInventoryMap[player]
    }

    fun isNotAvailableItem(itemStack: ItemStack?): Boolean {
        return itemStack == null || itemStack == nullItem
    }

    fun openSuggestionInventory(viewer: Player) {
        initSuggestionInventory(viewer)
        viewer.openInventory(suggestionInventory)
    }

    fun openSuggestionListInventory(viewer: Player) {
        val validIndex = (10..16).toList()

        initSuggestionListInventory()

        var tmp = 0
        for (suggestion in SuggestionHandler.suggester.keys) {
            suggestionListInventory.setItem(validIndex[tmp], SuggestionHandler.getGUIDescriptionOf(suggestion))

            tmp += 1
            if (tmp >= 7) {
                break
            }
        }

        viewer.openInventory(suggestionListInventory)
    }

    fun openSettingWandInventory(viewer: Player) {
        initSettingWandInventory(viewer)
        viewer.openInventory(settingWandInventoryMap[viewer]!!)
    }

    fun openVirtualChest(viewer: Player) {
        if (!WandHandler.isWandPlayerChoice(WandHandler.virtualChest, viewer)) return

        if (virtualChestInventoryMap[viewer] == null) {
            initVirtualChest(viewer)
        }

        viewer.openInventory(virtualChestInventoryMap[viewer]!!)
    }

    private fun fillGUIInventoryExcept(indexes: List<Int>, guiInventory: Inventory) {
        guiInventory.forEachIndexed { index, _ ->
            if (index !in indexes) guiInventory.setItem(index, nullItem)
            else guiInventory.setItem(index, null)
        }
    }

    private fun initSuggestionInventory(viewer: Player) {
        fillGUIInventoryExcept(listOf(1, 7), suggestionInventory)

        val difficultyIndicator =
            DifficultyTag.EASY.getIndicator(ScoreHandler.computeAdditionalScore(viewer))
        suggestionInventory.setItem(7, difficultyIndicator)
    }

    private fun initSuggestionListInventory() {
        fillGUIInventoryExcept((10..16).toList(), suggestionListInventory)
    }

    fun initSettingWandInventory(player: Player) {
        if (settingWandInventoryMap[player] == null) {
            val inventory = Bukkit.createInventory(
                null, COLUMNS * 3, Component.text("도구 설정")
            )
            settingWandInventoryMap[player] = inventory
            initSettingWandInventory(player)
            return
        }

        val validIndex = ((10..13) + (15..16)).toList()
        fillGUIInventoryExcept(validIndex, settingWandInventoryMap[player]!!)
        WandHandler.helperToolList.forEachIndexed { index, it ->
            val item = it.getWandAsItemStackWithoutEnchant()
            if (WandHandler.isWandPlayerChoice(it, player)) {
                item.addUnsafeEnchantment(
                    Enchantment.LOYALTY, 1
                )
            }
            settingWandInventoryMap[player]!!.setItem(validIndex[index], item)
        }
    }

    private fun initVirtualChest(player: Player) {
        val inventory = Bukkit.createInventory(
            null, COLUMNS, Component.text("개인 보관함")
        )
        virtualChestInventoryMap[player] = inventory
    }
}