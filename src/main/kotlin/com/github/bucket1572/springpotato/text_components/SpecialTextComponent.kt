package com.github.bucket1572.springpotato.text_components

import com.github.bucket1572.springpotato.colors.ColorTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.format.TextDecorationAndState

sealed class SpecialTextComponent(
    val text: String, val color: ColorTag, val textDecorationWithFlag: HashMap<TextDecoration, TextDecoration.State>
) {
    fun getComponent(): Component {
        return Component.text(text).style(getStyle())
    }

    private fun getStyle(): Style {
        return Style.empty().apply {
            textDecorationWithFlag.forEach { (decoration, decorationFlag) ->
                decoration(decoration, decorationFlag)
            }
            color(TextColor.fromHexString(color.hexString))
        }
    }
}

