package com.github.bucket1572.springpotato.common

import com.github.bucket1572.springpotato.handlers.WandNames
import com.github.bucket1572.springpotato.handlers.Wand
import com.github.bucket1572.springpotato.text_components.DescriptionComponent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

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

    const val SUGGESTION_WAND_COOLDOWN = 2 * 1200

    fun isSuggestionWand(item: ItemStack?): Boolean {
        return item != null && item.type == suggestionWand.material
                && item.itemMeta.displayName() == suggestionWand.name.component
    }

    fun isSuggestionListWand(item: ItemStack?): Boolean {
        return item != null && item.type == suggestionListWand.material
                && item.itemMeta.displayName() == suggestionListWand.name.component
    }
}