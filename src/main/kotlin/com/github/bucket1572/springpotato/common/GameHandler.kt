package com.github.bucket1572.springpotato.common

import com.github.bucket1572.springpotato.SpringPotato
import com.github.bucket1572.springpotato.game_phase.GamePhase
import com.github.bucket1572.springpotato.text_components.AlertComponent
import com.github.bucket1572.springpotato.text_components.DescriptionComponent
import com.github.bucket1572.springpotato.text_components.SuccessComponent
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Server

object GameHandler {
    private var gamePhase = GamePhase.NONE

    fun changeToSettingPhase(server: Server) {
        gamePhase = GamePhase.SETTING
        WandHandler.initSettingPhaseWand()
        clearWand(server)
        dispenseWand(server)
    }

    fun changeToMainPhase(
        plugin: SpringPotato, server: Server, mainGameTime: Int, buzzerBeaterTime: Int, lastHandoutTime: Int)
    {
        gamePhase = GamePhase.MAIN
        clearInventory(server)
        WandHandler.initMainPhaseWand()
        dispenseWand(server)
        initScore(server)
        announceGameStart(server, mainGameTime)
        Bukkit.getScheduler().runTaskLater(
            plugin,
            { -> changeToBuzzerBeaterPhase(plugin, server, buzzerBeaterTime, lastHandoutTime)},
            mainGameTime * 1200L
        )
    }

    fun changeToBuzzerBeaterPhase(
        plugin: SpringPotato, server: Server, buzzerBeaterTime: Int, lastHandoutTime: Int
    ) {
        gamePhase = GamePhase.BUZZER_BEATER
        announceGameBuzzerBeater(server)
        Bukkit.getScheduler().runTaskLater(
            plugin,
            { -> changeToLastHandOutPhase(plugin, server, lastHandoutTime)},
            buzzerBeaterTime * 1200L
        )
    }

    fun changeToLastHandOutPhase(plugin: SpringPotato, server: Server, lastHandoutTime: Int) {
        gamePhase = GamePhase.LAST_HANDOUT
        announceGameLastHandout(server)
        Bukkit.getScheduler().runTaskLater(
            plugin, { -> endGame(server)}, lastHandoutTime * 1200L
        )
    }

    fun endGame(server: Server) {
        gamePhase = GamePhase.END
        announceGameEnd(server)
        gamePhase = GamePhase.NONE
    }

    fun isRunning(): Boolean {
        return gamePhase != GamePhase.NONE
    }

    fun isSettingPhase(): Boolean {
        return gamePhase == GamePhase.SETTING
    }

    fun isMainPhase(): Boolean {
        return gamePhase == GamePhase.MAIN
    }

    fun isBuzzerBeaterPhase(): Boolean {
        return gamePhase == GamePhase.BUZZER_BEATER
    }

    fun isLastHandOutPhase(): Boolean {
        return gamePhase == GamePhase.LAST_HANDOUT
    }

    fun isHandOutPhase(): Boolean {
        return isMainPhase() || isBuzzerBeaterPhase() || isLastHandOutPhase()
    }

    fun isSuggestionPhase(): Boolean {
        return isMainPhase() || isBuzzerBeaterPhase()
    }

    private fun announceGameStart(server: Server, playTime: Int) {
        server.showTitle(
            Title.title(
                SuccessComponent("게임이 시작되었습니다.").getComponent(),
                DescriptionComponent("게임 시간: ${playTime}분").getComponent()
            )
        )
    }

    private fun announceGameEnd(server: Server) {
        server.showTitle(
            Title.title(
                AlertComponent("게임이 끝났습니다.").getComponent(),
                DescriptionComponent(" ").getComponent()
            )
        )
    }

    private fun announceGameBuzzerBeater(server: Server) {
        server.showTitle(
            Title.title(
                AlertComponent("막판 스퍼트!").getComponent(),
                DescriptionComponent("남은 시간동안 추가 점수가 늘어납니다.").getComponent()
            )
        )
    }

    private fun announceGameLastHandout(server: Server) {
        server.showTitle(
            Title.title(
                AlertComponent("마지막 견제 찬스").getComponent(),
                DescriptionComponent("더 이상 제안할 수는 없습니다. 상대방을 견제하세요!").getComponent()
            )
        )
    }

    private fun initScore(server: Server) {
        ScoreHandler.newScoreBoard()
        server.onlinePlayers.forEach { ScoreHandler.initScore(it) }
    }

    private fun dispenseWand(server: Server) {
        server.onlinePlayers.forEach { player ->
            WandHandler.giveWand(player)
        }
    }

    private fun clearWand(server: Server) {
        server.onlinePlayers.forEach {
            WandHandler.clearPlayerWand(it)
        }
    }

    private fun clearInventory(server: Server) {
        server.onlinePlayers.forEach {
            it.inventory.clear()
        }
    }
}