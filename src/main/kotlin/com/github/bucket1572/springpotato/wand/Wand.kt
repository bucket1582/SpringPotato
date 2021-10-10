package com.github.bucket1572.springpotato.wand

import com.github.bucket1572.springpotato.basic_logic.types.ItemType
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

data class Wand(
    val name: WandNames, val description: List<Component>, val material: Material, val cooldown: Int = 0,
    val enchantment: Enchantment? = null, val enchantmentLevel: Int = 0
) {
    fun getWandAsItemStack(): ItemStack {
        val itemStack = ItemStack(material)
        itemStack.editMeta {
            it.displayName(name.component)
            it.lore(
                listOf(ItemType.HELPING_TOOL.typeComponent.getComponent()) + description
            )
        }
        enchantment?.apply {
            itemStack.addUnsafeEnchantment(this, enchantmentLevel)
        }
        return itemStack
    }

    fun getWandAsItemStackWithoutEnchant(): ItemStack {
        val itemStack = ItemStack(material)
        itemStack.editMeta {
            it.displayName(name.component)
            it.lore(
                listOf(ItemType.HELPING_TOOL.typeComponent.getComponent()) + description
            )
        }
        return itemStack
    }
}