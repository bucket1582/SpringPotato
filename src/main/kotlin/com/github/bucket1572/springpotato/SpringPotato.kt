package com.github.bucket1572.springpotato

import com.github.bucket1572.springpotato.common.GameHandler
import com.github.bucket1572.springpotato.common.WandHandler
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
                                        this@SpringPotato, server,
                                        mainGameTime, buzzerBeaterTime, lastHandoutTime
                                    )

                                    this.player.world.worldBorder.center = this.player.location
                                    this.player.world.worldBorder.size = playRadius.toDouble()
                                    this.player.world.spawnLocation = this.player.location
                                    this.player.world.setGameRule(GameRule.SPAWN_RADIUS, playRadius / 2)
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