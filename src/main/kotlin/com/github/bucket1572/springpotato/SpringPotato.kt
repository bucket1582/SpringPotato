package com.github.bucket1572.springpotato

import com.github.bucket1572.springpotato.basic_logic.handlers.GameHandler
import com.github.bucket1572.springpotato.wand.handlers.WandHandler
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.GameRule
import org.bukkit.plugin.java.JavaPlugin

class SpringPotato : JavaPlugin() {
    override fun onEnable() {
        // 초기화
        EventListenerLoader.loadAllEventListeners(this)
        registerCommands()
    }

    private fun registerCommands() = kommand {
        register("potato") {
            then("setting") {
                requires {
                    (this.isPlayer && this.isOp) || (this.isConsole)
                }
                executes {
                    if (GameHandler.isRunning()) return@executes
                    GameHandler.changeToSettingPhase(server)
                }
            }
            then("start") {
                then("mainGameTime" to int(minimum = 0)) {
                    then("buzzerBeaterTime" to int(minimum = 0)) {
                        then("lastHandoutTime" to int(minimum = 0)) {
                            then("playRadius" to int(minimum = 0)) {
                                requires {
                                    (this.isPlayer && this.isOp) || (this.isConsole)
                                }
                                executes {
                                    if (!GameHandler.isSettingPhase()) return@executes
                                    val mainGameTime: Int by it
                                    val buzzerBeaterTime: Int by it
                                    val lastHandoutTime: Int by it
                                    val playRadius: Int by it
                                    GameHandler.changeToMainPhase(
                                        this@SpringPotato, server, this.player.location, playRadius,
                                        mainGameTime, buzzerBeaterTime, lastHandoutTime
                                    )
                                }
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
                    if (!GameHandler.isRunning()) return@executes
                    GameHandler.endGame(server)
                }
            }
            then("handlers") {
                requires {
                    this.isPlayer
                }
                executes {
                    if (!GameHandler.isRunning()) return@executes
                    WandHandler.giveWand(player)
                }
            }
        }
    }
}