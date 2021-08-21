package com.github.bucket1572.springpotato.difficulty_indicator.tag

import com.github.bucket1572.springpotato.colors.ColorTag
import com.github.bucket1572.springpotato.colors.getTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

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