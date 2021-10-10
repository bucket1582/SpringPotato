package com.github.bucket1572.springpotato.common.difficulty_indicator.tag

import com.github.bucket1572.springpotato.common.colors.ColorTag
import com.github.bucket1572.springpotato.common.colors.getTextColor
import com.github.bucket1572.springpotato.common.difficulty_indicator.EasyIndexComponent
import com.github.bucket1572.springpotato.common.difficulty_indicator.HardIndexComponent
import com.github.bucket1572.springpotato.common.difficulty_indicator.IntermediateIndexComponent
import com.github.bucket1572.springpotato.common.text_components.DescriptionComponent
import com.github.bucket1572.springpotato.basic_logic.types.ItemType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class DifficultyTag {
    EASY, INTERMEDIATE, HARD
}

fun DifficultyTag.getStyle(): Style =
    when (this) {
        DifficultyTag.EASY -> Style.empty().color(ColorTag.EASY.getTextColor())
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
        DifficultyTag.INTERMEDIATE -> Style.empty().color(ColorTag.INTERMEDIATE.getTextColor())
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
        DifficultyTag.HARD -> Style.empty().color(ColorTag.HARD.getTextColor())
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
    }

fun DifficultyTag.getComponent(text: String): Component =
    when (this) {
        DifficultyTag.EASY -> EasyIndexComponent(text).getComponent()
        DifficultyTag.INTERMEDIATE -> IntermediateIndexComponent(text).getComponent()
        DifficultyTag.HARD -> HardIndexComponent(text).getComponent()
    }

fun DifficultyTag.getFundamentalPoint(): Int =
    when (this) {
        DifficultyTag.EASY -> 2
        DifficultyTag.INTERMEDIATE -> 5
        DifficultyTag.HARD -> 12
    }

fun DifficultyTag.getSuggestingTime(): Int =
    when (this) {
        DifficultyTag.EASY -> 3
        DifficultyTag.INTERMEDIATE -> 5
        DifficultyTag.HARD -> 10
    }

fun DifficultyTag.getMaterial(): Material =
    when (this) {
        DifficultyTag.EASY -> Material.LIME_CONCRETE
        DifficultyTag.INTERMEDIATE -> Material.YELLOW_CONCRETE
        DifficultyTag.HARD -> Material.RED_CONCRETE
    }

fun DifficultyTag.getIndicatorItem(): ItemStack = ItemStack(this.getMaterial(), 1)

fun DifficultyTag.getIndicator(additionalPoint: Int): ItemStack {
    val lore = listOf(
        ItemType.DIFFICULTY_INDICATOR.typeComponent.getComponent(),
        DescriptionComponent("시간: ${this.getSuggestingTime()}").getComponent(),
        DescriptionComponent(
            "점수: ${this.getFundamentalPoint()}${if (additionalPoint <= 0) "" else "(+$additionalPoint)"}"
        ).getComponent()
    )

    val indicator = this.getIndicatorItem()

    when (this) {
        DifficultyTag.EASY -> {
            indicator.editMeta {
                it.displayName(EasyIndexComponent("쉬움").getComponent())
                it.lore(lore)
            }
        }
        DifficultyTag.INTERMEDIATE -> {
            indicator.editMeta {
                it.displayName(IntermediateIndexComponent("보통").getComponent())
                it.lore(lore)
            }
        }
        DifficultyTag.HARD -> {
            indicator.editMeta {
                it.displayName(HardIndexComponent("어려움").getComponent())
                it.lore(lore)
            }
        }
    }

    return indicator
}