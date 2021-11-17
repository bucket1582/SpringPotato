package com.github.bucket1572.springpotato

import com.github.bucket1572.springpotato.basic_logic.handlers.GameHandler
import com.github.bucket1572.springpotato.common.text_components.AlertComponent
import com.github.bucket1572.springpotato.wand.handlers.WandHandler
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
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
                            then("playDiameter" to int(minimum = 0)) {
                                requires {
                                    (this.isPlayer && this.isOp) || (this.isConsole)
                                }
                                executes {
                                    if (!GameHandler.isSettingPhase()) return@executes
                                    val mainGameTime: Int by it
                                    val buzzerBeaterTime: Int by it
                                    val lastHandoutTime: Int by it
                                    val playDiameter: Int by it

                                    // Validation
                                    if (playDiameter < GameHandler.SPAWN_RADIUS * 2) {
                                        this.player.sendMessage(
                                            AlertComponent("게임 구역은 최소 ${GameHandler.SPAWN_RADIUS * 2}의 직경을 가지고 있어야 합니다.").getComponent()
                                        )
                                        return@executes
                                    }

                                    GameHandler.changeToMainPhase(
                                        this@SpringPotato, server, this.player.location, playDiameter,
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