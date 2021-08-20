package com.github.bucket1572.springpotato

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

enum class ItemNames(val component: Component) {
    SUGGESTION_HANDLER(Component.text("제안", TextColor.fromHexString("#cc8aff"))),
    SUGGESTION_LIST_HANDLER(Component.text("제안 목록", TextColor.fromHexString("#a42eff")))
}