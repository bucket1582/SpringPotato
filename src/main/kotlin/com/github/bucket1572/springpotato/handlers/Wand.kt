package com.github.bucket1572.springpotato.handlers

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class Wand(val name: WandNames, val description: List<Component>, val material: Material) {
    fun getWandAsItemStack(): ItemStack {
        val itemStack = ItemStack(material)
        itemStack.editMeta {
            it.displayName(name.component)
            it.lore(description)
        }
        return itemStack
    }
}
