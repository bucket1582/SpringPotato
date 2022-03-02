package com.github.bucket1582.springpotato.common.difficulty_indicator

import com.github.bucket1582.springpotato.common.difficulty_indicator.tag.DifficultyTag
import com.github.bucket1582.springpotato.common.difficulty_indicator.tag.getStyle
import net.kyori.adventure.text.Component

sealed class DifficultyIndicatorComponent(val text: String, private val difficultyIndex: DifficultyTag) {
    fun getComponent(): Component {
        return Component.text(text).style(difficultyIndex.getStyle())
    }
}

data class EasyIndexComponent(val title: String): DifficultyIndicatorComponent(
    text = title,
    difficultyIndex = DifficultyTag.EASY
)

data class IntermediateIndexComponent(val title: String): DifficultyIndicatorComponent(
    text = title,
    difficultyIndex = DifficultyTag.INTERMEDIATE
)

data class HardIndexComponent(val title: String): DifficultyIndicatorComponent(
    text = title,
    difficultyIndex = DifficultyTag.HARD
)
