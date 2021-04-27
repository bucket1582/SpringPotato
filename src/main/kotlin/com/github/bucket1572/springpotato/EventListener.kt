package com.github.bucket1572.springpotato

import com.destroystokyo.paper.Title
import com.github.noonmaru.kommand.argument.player
import com.github.noonmaru.tap.effect.playFirework
import com.github.noonmaru.tap.v1_16_R3.protocol.NMSPacketSupport
import net.md_5.bungee.api.ChatColor
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.ItemMeta
import java.time.LocalTime
import java.time.LocalTime.now
import kotlin.collections.ArrayList

class EventListener : Listener {
    var plugin: SpringPotato? = null
    val itemMap: MutableMap<Material, Int> = mutableMapOf()
    val playerDone: MutableMap<Material, ArrayList<Player>> = mutableMapOf()
    val suggester: MutableMap<Material, Player> = mutableMapOf()
    val suggestionTime: MutableMap<Material, LocalTime> = mutableMapOf()
    val cooldown: Int = 5

    private val suggestionInventory = Bukkit.createInventory(
        null, InventoryType.DISPENSER, "제안"
    )

    private val easyIdx = ItemStack(
        Material.LIME_CONCRETE, 1
    )
    private val easyTime = 1
    private val easyPoint = 2

    private val intermediateIdx = ItemStack(
        Material.YELLOW_CONCRETE, 1
    )
    private val intermediateTime = 3
    private val intermediatePoint = 5

    private val hardIdx = ItemStack(
        Material.RED_CONCRETE, 1
    )
    private val hardTime = 5
    private val hardPoint = 10

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
            // 쿨타임일 경우
            else if (player.getCooldown(Material.NETHER_STAR) > 0) {
                player.sendActionBar("${ChatColor.RED}아직 쿨타임이 끝나지 않았습니다.")
            }
            // 아닐 경우
            else {
                // 제안 초기화
                for (i in 0..8) {
                    if (i == 1) {
                        suggestionInventory.setItem(i, null)
                    } else if (i != 7) {
                        suggestionInventory.setItem(i, nullItem)
                    } else {
                        suggestionInventory.setItem(i, easyIdx)
                    }
                }
                player.openInventory(suggestionInventory)
            }
        }
    }

    @EventHandler
    fun onReadingSuggestionList(event: PlayerInteractEvent) {
        // 게임 시작 전에는 이벤트를 적용하지 않음.
        if (plugin?.isRunning != true) return

        // 초기화
        val action = event.action
        val player = event.player
        val itemInMain = player.inventory.itemInMainHand

        // 제안 목록 열람
        if (action == Action.RIGHT_CLICK_AIR &&
            itemInMain.type == Material.NETHER_STAR &&
            itemInMain.itemMeta?.displayName == "${ChatColor.LIGHT_PURPLE}제안 목록"
        ) {
            val book = ItemStack(Material.WRITTEN_BOOK)
            book.apply {
                val meta = itemMeta as BookMeta
                var dummy = 0

                var page = ""
                val time = now()
                val timeSec = time.hour * 3600 + time.minute * 60 + time.second
                for (suggestion in suggester.keys) {
                    val suggestionPeriod = suggestionTime[suggestion]!!
                    val perSec = suggestionPeriod.hour * 3600 +
                            suggestionPeriod.minute * 60 +
                            suggestionPeriod.second
                    val duration = timeSec - perSec

                    val color = suggestion.let {
                        when (itemMap[it]) {
                            easyPoint -> ChatColor.GREEN
                            intermediatePoint -> ChatColor.YELLOW
                            hardPoint -> ChatColor.RED
                            else -> ChatColor.WHITE
                        }
                    }
                    val remains = suggestion.let {
                        when (itemMap[it]) {
                            easyPoint -> easyTime * 60 - duration
                            intermediatePoint -> intermediateTime * 60 - duration
                            hardPoint -> hardTime * 60 - duration
                            else -> 0
                        }
                    }
                    val minute = remains / 60
                    val second = remains % 60

                    page += "$color${suggestion.name}:\n${ChatColor.BLACK}$minute 분 $second 초 남음\n"
                    dummy += 1
                    if (dummy > 4) {
                        break
                    }
                }

                meta.pages = listOf(page)
                meta.author = "server"
                meta.title = "제안 목록"
                itemMeta = meta
            }
            player.openBook(book)
        }
    }

    @EventHandler
    fun onSuggestion(event: InventoryClickEvent) {
        // 게임 시작 전에는 이벤트를 적용하지 않음.
        if (plugin?.isRunning != true) return

        val inventory = event.inventory
        // 제안 인벤토리 핸들링
        if (inventory == suggestionInventory) {
            val action = event.action
            // 7번 슬롯 클릭 핸들링
            if (action == InventoryAction.PICKUP_ALL) {
                val clicked = event.slot
                val clickedType = event.slotType
                // 7번 슬롯을 클릭했을 경우
                if (clicked == 7 && clickedType == InventoryType.SlotType.CONTAINER) {
                    val index = inventory.contents[7]
                    // 난이도 하 -> 중 -> 상
                    when (index.type) {
                        Material.LIME_CONCRETE -> {
                            event.currentItem = intermediateIdx
                        }
                        Material.YELLOW_CONCRETE -> {
                            event.currentItem = hardIdx
                        }
                        Material.RED_CONCRETE -> {
                            event.currentItem = easyIdx
                        }
                        else -> {

                        }
                    }
                    event.isCancelled = true
                }
            }
            // 다른 경우
            else {
                // 1. 플레이어가 인벤토리 내부에서 place 하는 것이 아니다.
                // 2. 플레이어가 제안 인벤토리 내의 1번 슬롯에 place one 하는 것이 아니다.
                // then -> Cancel
                val clicked = event.slot
                val clickedType = event.slotType

                fun isPlacement(action: InventoryAction) =
                    when (action) {
                        InventoryAction.PLACE_ONE, InventoryAction.PLACE_ALL, InventoryAction.PLACE_SOME -> {
                            true
                        }
                        else -> {
                            false
                        }
                    }

                if (!((clickedType == InventoryType.SlotType.QUICKBAR || clicked > 8) && (isPlacement(action))) &&
                    !((clicked == 1) && (action == InventoryAction.PLACE_ONE))
                ) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onHandoutSuggestion(event: InventoryCloseEvent) {
        // 게임 시작 전에는 이벤트를 적용하지 않음.
        if (plugin?.isRunning != true) return

        val inventory = event.inventory
        if (inventory == suggestionInventory) {
            // 제안한 아이템과 난이도
            val item = inventory.contents[1] ?: return

            // 이미 제안한 아이템일 경우
            if (item.type in itemMap.keys) {
                event.player.sendMessage("${ChatColor.RED}이미 제안이 존재합니다. 아이템은 돌려드리지 않습니다.")
                return
            }

            val point = inventory.contents[7]?.type.let {
                when (it) {
                    Material.LIME_CONCRETE -> {easyPoint}
                    Material.YELLOW_CONCRETE -> {intermediatePoint}
                    Material.RED_CONCRETE -> {hardPoint}
                    else -> {return}
                }
            }
            val time = inventory.contents[7]?.type.let {
                when (it) {
                    Material.LIME_CONCRETE -> {easyTime}
                    Material.YELLOW_CONCRETE -> {intermediateTime}
                    Material.RED_CONCRETE -> {hardTime}
                    else -> {return}
                }
            }

            // 아이템과 난이도 공표
            when (point) {
                easyPoint -> {
                    val name = item.type.name
                    val title = Title.Builder().apply {
                        fadeIn(5)
                        fadeOut(5)
                        title("${ChatColor.GREEN}느 집엔 이거 없지?")
                        subtitle("${ChatColor.WHITE} $name 제출 : 시간 제한 ${easyTime}분")
                        stay(40)
                    }.build()
                    title.broadcast()
                }
                intermediatePoint -> {
                    val name = item.type.name
                    val title = Title.Builder().apply {
                        fadeIn(5)
                        fadeOut(5)
                        title("${ChatColor.YELLOW}느 집엔 이거 없지?")
                        subtitle("${ChatColor.WHITE} $name 제출 : 시간 제한 ${intermediateTime}분")
                        stay(40)
                    }.build()
                    title.broadcast()
                }
                hardPoint -> {
                    val name = item.type.name
                    val title = Title.Builder().apply {
                        fadeIn(5)
                        fadeOut(5)
                        title("${ChatColor.RED}느 집엔 이거 없지?")
                        subtitle("${ChatColor.WHITE} $name 제출 : 시간 제한 ${hardTime}분")
                        stay(40)
                    }.build()
                    title.broadcast()
                }
                else -> {}
            }

            event.player.setCooldown(Material.NETHER_STAR, cooldown * 1200)

            // map 에 추가하고, 시간 지나면 판정 및 없애기
            itemMap[item.type] = point
            playerDone[item.type] = ArrayList()
            suggester[item.type] = event.player as Player
            suggestionTime[item.type] = now()

            val deleteItem = Runnable {
                var isMade = 0
                for (player in plugin!!.server.onlinePlayers) {
                    if (player in playerDone[item.type]!!) {
                        val board = plugin!!.scoreboard!!
                        val objective = board.getObjective("점수")!!
                        val score = objective.getScore(player.name)
                        score.score += 1
                        isMade += 1
                    }
                    player.sendMessage("${ChatColor.RED}${item.type.name} 제출 기한 만료!")
                }

                // 아무도 못 만들었을 경우 +
                if (isMade == 0) {
                    val player = suggester[item.type]!!
                    val board = plugin!!.scoreboard!!
                    val objective = board.getObjective("점수")!!
                    val score = objective.getScore(player.name)
                    score.score += point
                }
                // 만든 사람 수에 비례해서 -
                else {
                    val player = suggester[item.type]!!
                    val board = plugin!!.scoreboard!!
                    val objective = board.getObjective("점수")!!
                    val score = objective.getScore(player.name)
                    score.score -= isMade * point
                }

                playerDone.remove(item.type)
                itemMap.remove(item.type)
                suggester.remove(item.type)
                suggestionTime.remove(item.type)
            }

            plugin?.apply {
                Bukkit.getScheduler().runTaskLater(this, deleteItem, time.toLong() * 1200)
            }
        }
    }

    @EventHandler
    fun onHandoutResult(event: PlayerInteractEvent) {
        // 게임 시작 전에는 이벤트를 적용하지 않음.
        if (plugin?.isRunning != true) return

        // 초기화
        val action = event.action
        val player = event.player
        val itemInMain = player.inventory.itemInMainHand

        // 우클릭으로 제출
        if (action == Action.RIGHT_CLICK_AIR) {
            if (itemInMain.type in itemMap.keys &&
                !(event.player in playerDone[itemInMain.type]!!)) {
                playerDone[itemInMain.type]!!.add(event.player)
                Bukkit.broadcastMessage("${ChatColor.GREEN}${event.player.name}님이 ${itemInMain.type.name}을 제출했습니다!")
                val firework = FireworkEffect.builder().apply {
                    trail(true)
                    flicker(false)
                    withColor(Color.GREEN, Color.YELLOW, Color.WHITE, Color.LIME)
                    with(FireworkEffect.Type.BALL)
                }.build()
                event.player.playSound(event.player.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.6f, 1.0f)
                event.player.world.playFirework(event.player.location, firework, 15.0)
            }
        }
    }
}