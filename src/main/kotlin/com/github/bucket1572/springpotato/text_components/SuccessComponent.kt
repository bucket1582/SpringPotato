package com.github.bucket1572.springpotato.text_components

import com.github.bucket1572.springpotato.colors.ColorTag
import net.kyori.adventure.text.format.TextDecoration

data class SuccessComponent(val message: String): SpecialTextComponent(
    text = message,
    color = ColorTag.SUCCESS,
    textDecorationWithFlag = hashMapOf(
        Pair(TextDecoration.BOLD, TextDecoration.State.TRUE)
    )
)