package com.github.bucket1572.springpotato.text_components

import com.github.bucket1572.springpotato.colors.ColorTag
import net.kyori.adventure.text.format.TextDecoration

// TODO: 2021-08-21 Difficulty Index Component 를 따로 구현할 것. -> when 유용성 
data class EasyIndexComponent(val title: String): SpecialTextComponent(
    text = title,
    color = ColorTag.EASY,
    textDecorationWithFlag = hashMapOf(
        Pair(TextDecoration.ITALIC, TextDecoration.State.FALSE),
        Pair(TextDecoration.BOLD, TextDecoration.State.TRUE)
    )
)