package com.github.bucket1572.springpotato

import io.github.monun.kommand.kommand
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard

class SpringPotato : JavaPlugin() {
    var isRunning: Boolean = false
    var suggestionHandler: ItemStack = ItemStack(
        Material.NETHER_STAR
    )
    var suggestionListHandler: ItemStack = ItemStack(
        Material.NETHER_STAR
    )
    var scoreboard: Scoreboard? = null

    init {
        suggestionHandler.apply {
            val meta = this.itemMeta
            meta.setDisplayName("${ChatColor.LIGHT_PURPLE}제안")
            meta.lore = listOf(
                "${ChatColor.WHITE}우클릭 시 제안 창을 열 수 있습니다."
            )
            this.itemMeta = meta
        }
        suggestionListHandler.apply {
            val meta = this.itemMeta
            meta.setDisplayName("${ChatColor.LIGHT_PURPLE}제안 목록")
            meta.lore = listOf(
                "${ChatColor.WHITE}우클릭 시 제안 목록을 알 수 있습니다."
            )
            this.itemMeta = meta
        }
    }

    override fun onEnable() {
        // 초기화
        val eventListener = EventListener()
        eventListener.plugin = this
        this.server.pluginManager.registerEvents(eventListener, this)

        registerCommands()
    }

    private fun registerCommands() = kommand {
        register("potato") {
            then("start") {
                executes {
                    if (!isRunning) {
                        isRunning = true

                        // 스코어보드 초기화
                        val board = Bukkit.getScoreboardManager().newScoreboard
                        val objective = board.registerNewObjective("점수", "dummy", "점수")
                        objective.displaySlot = DisplaySlot.SIDEBAR
                        scoreboard = board

                        // 플레이어 초기화
                        for (player in this@SpringPotato.server.onlinePlayers) {
                            player.inventory.addItem(suggestionHandler)
                            player.inventory.addItem(suggestionListHandler)
                            val score = objective.getScore(player.name)
                            score.score = 0
                            player.scoreboard = board
                        }
                    }
                }
            }
            then("stop") {
                executes {
                    if (isRunning) {
                        isRunning = false
                    }
                }
            }
            then("handlers") {
                executes {
                    if (isRunning) {
                        for (player in server.onlinePlayers) {
                            if (!(suggestionHandler in player.inventory.contents)) {
                                player.inventory.addItem(suggestionHandler)
                            }
                            if (!(suggestionListHandler in player.inventory.contents)) {
                                player.inventory.addItem(suggestionListHandler)
                            }
                        }
                    }
                }
            }
        }
    }
}