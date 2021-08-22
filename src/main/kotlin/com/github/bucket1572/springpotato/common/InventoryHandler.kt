package com.github.bucket1572.springpotato.common

import com.github.bucket1572.springpotato.difficulty_indicator.tag.DifficultyTag
import com.github.bucket1572.springpotato.difficulty_indicator.tag.getIndicator
import com.github.bucket1572.springpotato.text_components.DescriptionComponent
import com.github.bucket1572.springpotato.type.ItemType
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
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

    init {
        initSuggestionInventory()
        initSuggestionListInventory()
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

    fun isNotAvailableItem(itemStack: ItemStack?): Boolean {
        return itemStack == null || itemStack == nullItem
    }

    fun openSuggestionInventory(viewer: Player) {
        initSuggestionInventory()

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


    private fun fillGUIInventoryExcept(indexes: List<Int>, guiInventory: Inventory) {
        guiInventory.forEachIndexed { index, _ ->
            if (index !in indexes) guiInventory.setItem(index, nullItem)
            else guiInventory.setItem(index, null)
        }
    }

    private fun initSuggestionInventory() {
        fillGUIInventoryExcept(listOf(1, 7), suggestionInventory)

        val difficultyIndicator = DifficultyTag.EASY.getIndicator(0)
        suggestionInventory.setItem(7, difficultyIndicator)
    }

    private fun initSuggestionListInventory() {
        fillGUIInventoryExcept((10..16).toList(), suggestionListInventory)
    }
}