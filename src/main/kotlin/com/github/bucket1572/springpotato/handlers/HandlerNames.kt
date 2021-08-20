package com.github.bucket1572.springpotato.handlers

import com.github.bucket1572.springpotato.text_components.CommonHandlerNameComponent
import net.kyori.adventure.text.Component

enum class HandlerNames(val component: Component) {
    SUGGESTION_HANDLER(
        CommonHandlerNameComponent("제안").getComponent()
    ),
    SUGGESTION_LIST_HANDLER(
        CommonHandlerNameComponent("제안 목록").getComponent()
    )
}