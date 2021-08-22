package com.github.bucket1572.springpotato

import com.github.bucket1572.springpotato.common.WandHandler
import com.github.bucket1572.springpotato.event_listeners.SuggestionGUIListener
import com.github.bucket1572.springpotato.event_listeners.SuggestionListGUIListener
import com.github.bucket1572.springpotato.handlers.Wand
import com.github.bucket1572.springpotato.handlers.WandNames
import com.github.bucket1572.springpotato.text_components.AlertComponent
import com.github.bucket1572.springpotato.text_components.DescriptionComponent
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import kotlin.math.roundToInt

class SpringPotato : JavaPlugin() {
    var isRunning: Boolean = false
    var scoreboard: Scoreboard? = null

    override fun onEnable() {
        // 초기화
        EventListenerLoader.loadAllEventListeners(this)
        registerCommands()
    }

    private fun registerCommands() = kommand {
        register("potato") {
            then("start") {
                then("playTime" to int(minimum = 0)) {
                    then("playRadius" to int(minimum = 0)) {
                        requires {
                            (this.isPlayer && this.isOp) || (this.isConsole)
                        }
                        executes {
                            if (!isRunning) {
                                val playTime: Int by it
                                val playRadius: Int by it
                                isRunning = true

                                // 스코어보드 초기화
                                val board = Bukkit.getScoreboardManager().newScoreboard
                                val objective = board.registerNewObjective(
                                    "점수", "dummy",
                                    Component.text("점수", TextColor.fromHexString("#deaa50"))
                                )
                                objective.displaySlot = DisplaySlot.SIDEBAR
                                scoreboard = board

                                // 플레이어 초기화
                                for (player in this@SpringPotato.server.onlinePlayers) {
                                    WandHandler.giveWand(player)
                                    val score = objective.getScore(player.name)
                                    score.score = 0
                                    player.scoreboard = board
                                }

                                // 게임 종료
                                val endGame = Runnable {
                                    if (isRunning) {
                                        player.server.showTitle(Title.title(
                                            AlertComponent("게임이 종료되었습니다.").getComponent(),
                                            DescriptionComponent(" ").getComponent()
                                        ))
                                        isRunning = false
                                    }
                                }
                                Bukkit.getScheduler().runTaskLater(this@SpringPotato, endGame, playTime * 1200L)
                                this.player.world.worldBorder.center = this.player.location
                                this.player.world.worldBorder.size = playRadius.toDouble()
                            }
                        }
                    }
                }
            }
            then("stop") {
                requires {
                    (this.isPlayer && this.isOp) || (this.isConsole)
                }
                executes {
                    if (isRunning) {
                        isRunning = false
                    }
                }
            }
            then("handlers") {
                requires {
                    this.isPlayer
                }
                executes {
                    if (isRunning) {
                        WandHandler.giveWand(player)
                    }
                }
            }
        }
    }
}