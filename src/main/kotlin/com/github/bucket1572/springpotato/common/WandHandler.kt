package com.github.bucket1572.springpotato.common

import com.github.bucket1572.springpotato.handlers.WandNames
import com.github.bucket1572.springpotato.handlers.Wand
import com.github.bucket1572.springpotato.text_components.DescriptionComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CompassMeta

object WandHandler {
    val suggestionWand = Wand(
        WandNames.SUGGESTION_WAND,
        listOf(
            DescriptionComponent("우클릭 시 제안 창을 열 수 있습니다.").getComponent()
        ),
        Material.NETHER_STAR
    )

    val suggestionListWand = Wand(
        WandNames.SUGGESTION_LIST_WAND,
        listOf(
            DescriptionComponent("우클릭 시 제안 목록을 확인할 수 있습니다.").getComponent()
        ),
        Material.BOOK
    )

    val trackWand = Wand(
        WandNames.TRACKER_WAND,
        listOf(
            DescriptionComponent("우클릭 시 추적 대상을 설정할 수 있습니다.").getComponent()
        ),
        Material.COMPASS
    )

    const val SUGGESTION_WAND_COOLDOWN = 2 * 1200

    const val TRACKER_WAND_COOLDOWN = 2 * 1200

    fun isSuggestionWand(item: ItemStack?): Boolean {
        return item != null && item.type == suggestionWand.material
                && item.itemMeta.displayName() == suggestionWand.name.component
    }

    fun isSuggestionListWand(item: ItemStack?): Boolean {
        return item != null && item.type == suggestionListWand.material
                && item.itemMeta.displayName() == suggestionListWand.name.component
    }

    fun isTrackerWand(item: ItemStack?): Boolean {
        return item != null && item.type == trackWand.material
                && item.itemMeta.displayName() == trackWand.name.component
    }

    fun isWand(item: ItemStack?): Boolean {
        return isSuggestionWand(item) || isSuggestionListWand(item) || isTrackerWand(item)
    }

    fun giveWand(player: Player) {
        if (!player.inventory.containsAtLeast(suggestionWand.getWandAsItemStack(), 1)) {
            player.inventory.addItem(suggestionWand.getWandAsItemStack())
        }

        if (!player.inventory.containsAtLeast(suggestionListWand.getWandAsItemStack(), 1)) {
            player.inventory.addItem(suggestionListWand.getWandAsItemStack())
        }

        if (!player.inventory.containsAtLeast(trackWand.getWandAsItemStack(), 1)) {
            player.inventory.addItem(trackWand.getWandAsItemStack())
        }
    }
}