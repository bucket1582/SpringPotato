package com.github.bucket1572.springpotato.text_components

import com.github.bucket1572.springpotato.colors.ColorTag
import com.github.bucket1572.springpotato.colors.getTextColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration

sealed class SpecialTextComponent(
    val text: String, val color: ColorTag, val decorationStyle: Style
) {
    fun getComponent(): Component {
        return Component.text(text).style(getStyle())
    }

    private fun getStyle(): Style {
        return decorationStyle.color(color.getTextColor())
    }
}

data class AlertComponent(val message: String) : SpecialTextComponent(
    text = message,
    color = ColorTag.ALERT,
    decorationStyle = Style.empty()
        .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
)

data class CommonHandlerNameComponent(val name: String) : SpecialTextComponent(
    text = name,
    color = ColorTag.COMMON_HANDLER_NAME,
    decorationStyle = Style.empty()
        .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
)

data class DescriptionComponent(val description: String) : SpecialTextComponent(
    text = description,
    color = ColorTag.DESCRIPTION,
    decorationStyle = Style.empty()
        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
)

data class TypeDescriptionComponent(val description: String) : SpecialTextComponent(
    text = description,
    color = ColorTag.TYPE_DESCRIPTION,
    decorationStyle = Style.empty()
        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
)

data class SuccessComponent(val message: String) : SpecialTextComponent(
    text = message,
    color = ColorTag.SUCCESS,
    decorationStyle = Style.empty()
        .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
)