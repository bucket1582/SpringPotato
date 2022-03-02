package com.github.bucket1582.springpotato.basic_logic.handlers

import com.github.bucket1582.springpotato.SpringPotato
import com.github.bucket1582.springpotato.wand.handlers.WandHandler
import com.github.bucket1582.springpotato.common.text_components.AlertComponent
import com.github.bucket1582.springpotato.common.text_components.DescriptionComponent
import com.github.bucket1582.springpotato.common.text_components.SuccessComponent
import com.github.bucket1582.springpotato.basic_logic.types.GamePhase
import com.github.bucket1582.springpotato.common.Timer
import com.github.bucket1582.springpotato.common.text_components.PhaseIndicatorComponent
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.title.Title
import org.bukkit.*

object GameHandler {
    const val SPAWN_RADIUS = 160
    private var gamePhase = GamePhase.NONE
    private var timer: Timer? = null

    fun changeToSettingPhase(server: Server) {
        gamePhase = GamePhase.SETTING
        clearInventory(server)
        WandHandler.initSettingPhaseWand()
        clearWand(server)
        dispenseWand(server)
    }

    fun changeToMainPhase(
        plugin: SpringPotato, server: Server, location: Location, playRadius: Int,
        mainGameTime: Int, buzzerBeaterTime: Int, lastHandoutTime: Int)
    {
        gamePhase = GamePhase.MAIN

        clearInventory(server)
        WandHandler.initMainPhaseWand()
        dispenseWand(server)

        initScore(server)

        announceGameStart(server, mainGameTime)

        setWorldBorder(location, playRadius)
        setSpawn(location, SPAWN_RADIUS)
        generalGameRule(server)

        setAndRunTimer(
            plugin, server, PhaseIndicatorComponent("Main"), BossBar.Color.GREEN, mainGameTime * 1200f, 1.0f
        ) { changeToBuzzerBeaterPhase(plugin, server, buzzerBeaterTime, lastHandoutTime) }

    }

    private fun changeToBuzzerBeaterPhase(
        plugin: SpringPotato, server: Server, buzzerBeaterTime: Int, lastHandoutTime: Int
    ) {
        gamePhase = GamePhase.BUZZER_BEATER

        announceGameBuzzerBeater(server)

        setAndRunTimer(
            plugin, server, PhaseIndicatorComponent("BuzzerBeater"), BossBar.Color.YELLOW, buzzerBeaterTime * 1200f, 1.0f
        ) { changeToLastHandOutPhase(plugin, server, lastHandoutTime) }
    }

    private fun changeToLastHandOutPhase(plugin: SpringPotato, server: Server, lastHandoutTime: Int) {
        gamePhase = GamePhase.LAST_HANDOUT
        announceGameLastHandout(server)

        setAndRunTimer(
            plugin, server, PhaseIndicatorComponent("HandOut"), BossBar.Color.RED, lastHandoutTime * 1200f, 1.0f
        ) { endGame(server) }
    }

    fun endGame(server: Server) {
        gamePhase = GamePhase.END
        announceGameEnd(server)
        gamePhase = GamePhase.NONE
        timer = null
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

    fun getPhase(): GamePhase = gamePhase

    fun getProgressRatio(): Float? = timer?.getProgressRatio()

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

    private fun setWorldBorder(location: Location, size: Int) {
        val worldBorder = location.world.worldBorder
        worldBorder.center = location.toCenterLocation()
        worldBorder.size = size.toDouble()
    }

    private fun setSpawn(location: Location, spawnRadius: Int) {
        location.world.spawnLocation = location
        location.world.setGameRule(GameRule.SPAWN_RADIUS, spawnRadius)
    }

    private fun generalGameRule(server: Server) {
        server.worlds.forEach {
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
            it.setGameRule(GameRule.KEEP_INVENTORY, true)
        }
    }

    private fun setAndRunTimer(
        plugin: SpringPotato, server: Server, name: PhaseIndicatorComponent, color: BossBar.Color,
        maxValue: Float, delta: Float, runnable: Runnable
    ) {
        timer?.deleteTimer()
        if (maxValue > 0) {
            timer = Timer(plugin, server, name.getComponent(), color, maxValue, delta, runnable)
            timer!!.runTimer()
        } else {
            runnable.run()
        }
    }
}