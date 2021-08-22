package com.github.bucket1572.springpotato.type

import com.github.bucket1572.springpotato.text_components.TypeDescriptionComponent

enum class ItemType(val typeComponent: TypeDescriptionComponent) {
    DIFFICULTY_INDICATOR(TypeDescriptionComponent("난이도 설정")),
    INDEX(TypeDescriptionComponent("인덱스"))
}