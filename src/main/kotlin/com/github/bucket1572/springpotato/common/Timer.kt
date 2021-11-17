package com.github.bucket1572.springpotato.common

import com.github.bucket1572.springpotato.SpringPotato
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.scheduler.BukkitTask

class Timer(
    val plugin: SpringPotato, val server: Server, val name: Component,
    color: Color, val maxValue: Float, val delta: Float, val runnable: Runnable
    ) {
    private var value = maxValue
    private var task: BukkitTask? = null
    private var bossBar = BossBar.bossBar(
        name, value / maxValue, color, BossBar.Overlay.PROGRESS
    )

    fun runTimer() {
        task = Bukkit.getScheduler().runTaskTimer(
            plugin, { -> this.progress() },0L, 1L
        )
        showBossBar()
    }

    fun getProgressRatio(): Float {
        return value / maxValue
    }

    fun deleteTimer() {
        bossBar.progress(0.0f)
        hideBossBar()
    }

    private fun showBossBar() {
        server.showBossBar(bossBar)
    }

    private fun hideBossBar() {
        server.hideBossBar(bossBar)
    }

    private fun progress() {
        value -= delta
        if (value <= 0.0f) {
            runnable.run()
            task?.cancel()
            hideBossBar()
        }
        bossBar.progress( value / maxValue )
    }
}