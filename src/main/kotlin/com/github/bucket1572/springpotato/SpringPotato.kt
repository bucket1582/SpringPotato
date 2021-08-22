package com.github.bucket1572.springpotato

import com.github.bucket1572.springpotato.common.WandHandler
import com.github.bucket1572.springpotato.event_listeners.SuggestionGUIListener
import com.github.bucket1572.springpotato.event_listeners.SuggestionListGUIListener
import com.github.bucket1572.springpotato.handlers.WandNames
import com.github.bucket1572.springpotato.text_components.DescriptionComponent
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
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
                requires {
                    this.isPlayer && this.isOp
                }
                executes {
                    if (!isRunning) {
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
                            player.inventory.addItem(WandHandler.suggestionWand.getWandAsItemStack())
                            player.inventory.addItem(WandHandler.suggestionListWand.getWandAsItemStack())
                            val score = objective.getScore(player.name)
                            score.score = 0
                            player.scoreboard = board
                        }
                    }
                }
            }
            then("stop") {
                requires {
                    this.isPlayer && this.isOp
                }
                executes {
                    if (isRunning) {
                        isRunning = false
                    }
                    this.player.closeInventory()
                    this.player.setCooldown(Material.NETHER_STAR, 0)
                }
            }
            then("handlers") {
                executes {
                    if (isRunning) {
                        player.inventory.addItem(WandHandler.suggestionWand.getWandAsItemStack())
                        player.inventory.addItem(WandHandler.suggestionListWand.getWandAsItemStack())
                    }
                }
            }
            then("showCoordinates") {
                then("playerNickname" to string()) {
                    executes {
                        val playerNickname: String by it
                        val playerLocationString: String
                        val targetPlayer: Player? = server.getPlayer(playerNickname)
                        playerLocationString = targetPlayer?.location?.let { loc ->
                            "차원: ${loc.world.name} X: ${loc.x.roundToInt()}, Y: ${loc.y.roundToInt()}, Z: ${loc.z.roundToInt()}"
                        }
                            ?: "해당 플레이어가 존재하지 않습니다."

                        player.sendMessage(playerLocationString)
                    }
                }
            }
        }
    }
}