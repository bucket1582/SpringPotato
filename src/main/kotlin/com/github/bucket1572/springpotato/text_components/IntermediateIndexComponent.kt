package com.github.bucket1572.springpotato.text_components

import com.github.bucket1572.springpotato.colors.ColorTag
import net.kyori.adventure.text.format.TextDecoration

data class IntermediateIndexComponent(val title: String): SpecialTextComponent(
    text = title,
    color = ColorTag.INTERMEDIATE,
    textDecorationWithFlag = hashMapOf(
        Pair(TextDecoration.ITALIC, TextDecoration.State.FALSE),
        Pair(TextDecoration.BOLD, TextDecoration.State.TRUE)
    )
)
