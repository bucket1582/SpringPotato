package com.github.bucket1582.springpotato.basic_logic.handlers

import com.github.bucket1582.springpotato.common.colors.ColorTag
import com.github.bucket1582.springpotato.common.colors.getTextColor
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard

object ScoreHandler {
    const val HANDOUT_SCORE = 1

    const val EXP_LEVEL_FOR_HANDOUT = 4

    const val SCORE_NAME = "점수"

    var scoreboard: Scoreboard? = null

    fun newScoreBoard() {
        val board = Bukkit.getScoreboardManager().newScoreboard
        val objective = board.registerNewObjective(
            SCORE_NAME, "dummy",
            Component.text("점수", ColorTag.SCOREBOARD.getTextColor())
        )
        objective.displaySlot = DisplaySlot.SIDEBAR
        scoreboard = board
    }

    fun updateScore(player: Player, amount: Int) {
        val scoreObjective = scoreboard!!.getObjective(SCORE_NAME)
        val score = scoreObjective!!.getScore(player.name)
        score.score += amount
    }

    fun setScore(player: Player, score: Int) {
        val scoreObjective = scoreboard!!.getObjective(SCORE_NAME)
        scoreObjective!!.getScore(player.name).score = score
    }

    fun initScore(player: Player) {
        setScore(player, 0)
        player.scoreboard = scoreboard!!
    }

    fun computeAdditionalScore(player: Player): Int {
        return if (!GameHandler.isBuzzerBeaterPhase()) player.level / 10 else player.level / 3
    }
}