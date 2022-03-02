package com.github.bucket1582.springpotato.basic_logic.limitations

import com.github.bucket1582.springpotato.common.text_components.LimitationDescriptionComponent

enum class LimitationType(val limitationComponent: LimitationDescriptionComponent) {
    NOT_BREAKING(LimitationDescriptionComponent("부서지지 않음")),
    VIRTUAL(LimitationDescriptionComponent("가상 아이템"))
}