package com.github.bucket1582.springpotato.basic_logic.types

import com.github.bucket1582.springpotato.common.text_components.TypeDescriptionComponent

enum class ItemType(val typeComponent: TypeDescriptionComponent) {
    DIFFICULTY_INDICATOR(TypeDescriptionComponent("난이도 설정")),
    HELPING_TOOL(TypeDescriptionComponent("도우미 도구"))
}