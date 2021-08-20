package com.github.bucket1572.springpotato.text_components

import com.github.bucket1572.springpotato.colors.ColorTag
import net.kyori.adventure.text.format.TextDecoration

data class DescriptionComponent(val description: String):
    SpecialTextComponent(
        text = description,
        color = ColorTag.DESCRIPTION,
        textDecorationWithFlag = hashMapOf(
            Pair(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        )
    )