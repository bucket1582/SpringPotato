package com.github.bucket1582.springpotato.wand

import com.github.bucket1582.springpotato.common.text_components.CommonHandlerNameComponent
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
    ),
    SETTING_WAND(
        CommonHandlerNameComponent("도구 선택").getComponent()
    ),
    LUCKY_SWORD(
        CommonHandlerNameComponent("행운이 깃든 칼").getComponent()
    ),
    EFFICIENT_AXE(
        CommonHandlerNameComponent("은도끼가 아닌 금도끼").getComponent()
    ),
    DURABLE_PICKAXE(
        CommonHandlerNameComponent("골드러시를 경험한 곡괭이").getComponent()
    ),
    RICHNESS_FISHING_ROD(
        CommonHandlerNameComponent("만선 기원 낚시대").getComponent()
    ),
    VIRTUAL_CHEST(
        CommonHandlerNameComponent("클라우드 가상 상자").getComponent()
    ),
    VIRTUAL_BED(
        CommonHandlerNameComponent("가구가 아닌 가상 침대").getComponent()
    )
}