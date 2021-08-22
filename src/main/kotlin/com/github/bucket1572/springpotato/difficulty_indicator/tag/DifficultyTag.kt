package com.github.bucket1572.springpotato.difficulty_indicator.tag

import com.github.bucket1572.springpotato.colors.ColorTag
import com.github.bucket1572.springpotato.colors.getTextColor
import com.github.bucket1572.springpotato.difficulty_indicator.EasyIndexComponent
import com.github.bucket1572.springpotato.difficulty_indicator.HardIndexComponent
import com.github.bucket1572.springpotato.difficulty_indicator.IntermediateIndexComponent
import com.github.bucket1572.springpotato.text_components.DescriptionComponent
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

fun DifficultyTag.getIndicatorItem(): ItemStack =
    when (this) {
        DifficultyTag.EASY -> ItemStack(Material.LIME_CONCRETE, 1)
        DifficultyTag.INTERMEDIATE -> ItemStack(Material.YELLOW_CONCRETE, 1)
        DifficultyTag.HARD -> ItemStack(Material.RED_CONCRETE, 1)
    }

fun DifficultyTag.getIndicator(additionalPoint: Int): ItemStack {
    val lore = listOf(
        DescriptionComponent("시간: ${this.getSuggestingTime()}").getComponent(),
        DescriptionComponent(
            "점수: ${this.getFundamentalPoint()}${if (additionalPoint <= 0) "" else "(+$additionalPoint"}"
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