package com.github.bucket1572.springpotato.limitation

import com.github.bucket1572.springpotato.text_components.LimitationDescriptionComponent

enum class LimitationType(val limitationComponent: LimitationDescriptionComponent) {
    NOT_BREAKING(LimitationDescriptionComponent("부서지지 않음")),
    VIRTUAL(LimitationDescriptionComponent("가상 아이템"))
}