package com.github.bucket1572.springpotato.handlers

import com.github.bucket1572.springpotato.text_components.CommonHandlerNameComponent
import net.kyori.adventure.text.Component

enum class WandNames(val component: Component) {
    SUGGESTION_WAND(
        CommonHandlerNameComponent("제안").getComponent()
    ),
    SUGGESTION_LIST_WAND(
        CommonHandlerNameComponent("제안 목록").getComponent()
    ),
    TRACKER_WAND(
        CommonHandlerNameComponent("모험의 나침반").getComponent()
    )
}