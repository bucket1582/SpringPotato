package com.github.bucket1572.springpotato

import com.github.noonmaru.kommand.kommand
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class SpringPotato : JavaPlugin(){
    var isRunning: Boolean = false
    var suggestionHandler: ItemStack = ItemStack(
        Material.NETHER_STAR
    )

    init {
        suggestionHandler.apply {
            val meta = this.itemMeta
            meta.setDisplayName("${ChatColor.LIGHT_PURPLE}제안")
            meta.lore = listOf(
                "${ChatColor.WHITE}우클릭 시 제안 창을 열 수 있습니다."
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
                executes{
                    if (!isRunning) {
                        isRunning = true
                        for (player in this@SpringPotato.server.onlinePlayers) {
                            player.inventory.addItem(suggestionHandler)
                        }
                    }
                }
            }
            then("stop"){
                executes {
                    if (isRunning) {
                        isRunning = false
                    }
                }
            }
        }
    }
}