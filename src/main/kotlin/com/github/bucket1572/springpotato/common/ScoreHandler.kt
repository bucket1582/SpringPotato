package com.github.bucket1572.springpotato.common

import com.github.bucket1572.springpotato.SpringPotato
import org.bukkit.entity.Player

object ScoreHandler {
    val handOutScore = 1

    fun updateScore(plugin: SpringPotato, player: Player, amount: Int) {
        val scoreObjective = plugin.scoreboard!!.getObjective("점수")
        val score = scoreObjective!!.getScore(player.name)
        score.score += amount
    }
}