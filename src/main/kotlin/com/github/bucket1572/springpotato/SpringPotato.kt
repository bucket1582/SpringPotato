package com.github.bucket1572.springpotato

import com.github.bucket1572.springpotato.handlers.HandlerNames
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
    private var suggestionHandler: ItemStack = ItemStack(
        Material.NETHER_STAR
    )
    private var suggestionListHandler: ItemStack = ItemStack(
        Material.NETHER_STAR
    )
    var scoreboard: Scoreboard? = null

    init {
        suggestionHandler.editMeta {
            it.displayName(HandlerNames.SUGGESTION_HANDLER.component)
            it.lore(
                listOf(
                    DescriptionComponent("우클릭 시 제안 창을 열 수 있습니다.").getComponent()
                )
            )
        }
        suggestionListHandler.editMeta {
            it.displayName(HandlerNames.SUGGESTION_LIST_HANDLER.component)
            it.lore(
                listOf(
                    DescriptionComponent("우클릭 시 제안 목록을 알 수 있습니다.").getComponent()
                )
            )
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
                        for (player in server.onlinePlayers) {
                            if (suggestionHandler !in player.inventory.contents) {
                                player.inventory.addItem(suggestionHandler)
                            }
                            if (suggestionListHandler !in player.inventory.contents) {
                                player.inventory.addItem(suggestionListHandler)
                            }
                        }
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