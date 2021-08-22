package com.github.bucket1572.springpotato.common

import com.github.bucket1572.springpotato.SpringPotato
import org.bukkit.entity.Player

object ScoreHandler {
    const val HANDOUT_SCORE = 1

    const val EXP_LEVEL_FOR_HANDOUT = 4

    fun updateScore(plugin: SpringPotato, player: Player, amount: Int) {
        val scoreObjective = plugin.scoreboard!!.getObjective("점수")
        val score = scoreObjective!!.getScore(player.name)
        score.score += amount
    }

    fun computeAdditionalScore(player: Player): Int {
        return player.level / 10
    }
}