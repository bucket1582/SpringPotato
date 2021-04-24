package com.github.bucket1572.springpotato

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class EventListener : Listener {
    var plugin: SpringPotato? = null

    private val suggestionInventory = Bukkit.createInventory(
        null, InventoryType.DISPENSER, "제안"
    )

    private val easyIdx = ItemStack(
        Material.LIME_CONCRETE, 1
    )
    private val easyTime = 1
    private val easyPoint = 1

    private val intermediateIdx = ItemStack(
        Material.YELLOW_CONCRETE, 1
    )
    private val intermediateTime = 3
    private val intermediatePoint = 4

    private val hardIdx = ItemStack(
        Material.RED_CONCRETE, 1
    )
    private val hardTime = 5
    private val hardPoint = 9

    private val nullItem = ItemStack(
        Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1
    )

    init {
        easyIdx.apply {
            val meta = this.itemMeta
            meta.setDisplayName("${ChatColor.GREEN}쉬움")
            meta.lore = listOf(
                "${ChatColor.WHITE}시간 : $easyTime",
                "${ChatColor.WHITE}점수 : $easyPoint"
            )

            this.itemMeta = meta
        }

        intermediateIdx.apply {
            val meta = this.itemMeta
            meta.setDisplayName("${ChatColor.YELLOW}보통")
            meta.lore = listOf(
                "${ChatColor.WHITE}시간 : $intermediateTime",
                "${ChatColor.WHITE}점수 : $intermediatePoint"
            )

            this.itemMeta = meta
        }

        hardIdx.apply {
            val meta = this.itemMeta
            meta.setDisplayName("${ChatColor.RED}어려움")
            meta.lore = listOf(
                "${ChatColor.WHITE}시간 : $hardTime",
                "${ChatColor.WHITE}점수 : $hardPoint"
            )

            this.itemMeta = meta
        }

        nullItem.apply {
            val meta = this.itemMeta
            meta.setDisplayName(" ")

            this.itemMeta = meta
        }
    }

    @EventHandler
    fun onOpeningSuggestionInventory(event: PlayerInteractEvent) {
        // 게임 시작 전에는 이벤트를 적용하지 않음.
        if (plugin?.isRunning != true) return

        // 초기화
        val action = event.action
        val player = event.player
        val itemInMain = player.inventory.itemInMainHand

        // 제안 전개
        if (action == Action.RIGHT_CLICK_AIR &&
            itemInMain.type == Material.NETHER_STAR &&
            itemInMain.itemMeta?.displayName == "${ChatColor.LIGHT_PURPLE}제안"
        ) {
            player.sendActionBar("제안 중...")
            // 다른 사람이 제안 중일 경우
            if (suggestionInventory.viewers.size > 0) {
                player.sendActionBar("${ChatColor.RED}현재 다른 사람이 제안 중입니다.")
            }
            // 아닐 경우
            else {
                // 제안 초기화
                for (i in 0..8){
                    if (i == 1){
                        continue
                    } else if (i == 7) {
                        suggestionInventory.setItem(i, easyIdx)
                    } else {
                        suggestionInventory.setItem(i, nullItem)
                    }
                }
                player.openInventory(suggestionInventory)
            }
        }
    }

    @EventHandler
    fun onSuggestion(event: InventoryClickEvent) {
        // 게임 시작 전에는 이벤트를 적용하지 않음.
        if (plugin?.isRunning != true) return

        val inventory = event.inventory
        if (inventory == suggestionInventory) {
            val clicked = event.slot
            if (clicked == 7) {
                val index = inventory.contents[7]
                when (index.type) {
                    Material.LIME_CONCRETE -> {
                        inventory.contents[7] = intermediateIdx
                    }
                    Material.YELLOW_CONCRETE -> {
                        inventory.contents[7] = hardIdx
                    }
                    Material.RED_CONCRETE -> {
                        inventory.contents[7] = easyIdx
                    }
                    else -> {
                        
                    }
                }
                event.isCancelled = true
            } else if (clicked == 2) {
                event.isCancelled = true
            } else {
                event.isCancelled = true
            }
        }
    }
}