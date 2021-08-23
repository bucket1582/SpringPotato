package com.github.bucket1572.springpotato.type

import com.github.bucket1572.springpotato.text_components.TypeDescriptionComponent
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

enum class ItemType(val typeComponent: TypeDescriptionComponent) {
    DIFFICULTY_INDICATOR(TypeDescriptionComponent("난이도 설정")),
    HELPING_TOOL(TypeDescriptionComponent("도우미 도구"))
}