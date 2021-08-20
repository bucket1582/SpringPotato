package com.github.bucket1572.springpotato

import com.github.bucket1572.springpotato.handlers.HandlerNames
import com.github.bucket1572.springpotato.text_components.*
import io.github.monun.tap.effect.playFirework
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
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
import java.time.LocalTime
import java.time.LocalTime.now

class EventListener : Listener {
    var plugin: SpringPotato? = null

    private val itemMap: MutableMap<Material, Int> = mutableMapOf()
    private val playerDone: MutableMap<Material, ArrayList<Player>> = mutableMapOf()
    private val suggester: MutableMap<Material, Player> = mutableMapOf()
    private val suggestionTime: MutableMap<Material, LocalTime> = mutableMapOf()
    private val cooldown: Int = 2

    private val suggestionInventory = Bukkit.createInventory(
        null, InventoryType.DISPENSER, Component.text("제안")
    )

    private val easyIdx = ItemStack(
        Material.LIME_CONCRETE, 1
    )
    private val easyTime = 3
    private val easyPoint = 2

    private val intermediateIdx = ItemStack(
        Material.YELLOW_CONCRETE, 1
    )
    private val intermediateTime = 5
    private val intermediatePoint = 5

    private val hardIdx = ItemStack(
        Material.RED_CONCRETE, 1
    )
    private val hardTime = 10
    private val hardPoint = 12

    private val nullItem = ItemStack(
        Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1
    )

    init {
        easyIdx.editMeta {
            it.displayName(EasyIndexComponent("쉬움").getComponent())
            it.lore(
                listOf(
                    DescriptionComponent("시간: $easyTime").getComponent(),
                    DescriptionComponent("점수: $easyPoint").getComponent()
                )
            )
        }
        intermediateIdx.editMeta {
            it.displayName(IntermediateIndexComponent("보통").getComponent())
            it.lore(
                listOf(
                    DescriptionComponent("시간: $intermediateTime").getComponent(),
                    DescriptionComponent("점수: $intermediatePoint").getComponent()
                )
            )
        }
        hardIdx.editMeta {
            it.displayName(HardIndexComponent("어려움").getComponent())
            it.lore(
                listOf(
                    DescriptionComponent("시간: $hardTime").getComponent(),
                    DescriptionComponent("점수: $hardPoint").getComponent()
                )
            )
        }
        nullItem.editMeta {
            it.displayName(Component.text(" "))
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
        // TODO: 2021-08-21 Handlers 의 Equality Checker 로 핸들러 일치 확인
        if (action == Action.RIGHT_CLICK_AIR &&
            itemInMain.type == Material.NETHER_STAR &&
            itemInMain.itemMeta.displayName() == HandlerNames.SUGGESTION_HANDLER.component
        ) {
            when {
                // 다른 사람이 제안 중일 경우
                suggestionInventory.viewers.size > 0 -> {
                    player.sendActionBar(AlertComponent("현재 다른 사람이 제안 중입니다.").getComponent())
                }
                // 쿨타임일 경우
                player.getCooldown(Material.NETHER_STAR) > 0 -> {
                    player.sendActionBar(AlertComponent("아직 쿨타임이 끝나지 않았습니다.").getComponent())
                }
                // 아닐 경우
                else -> {
                    // 제안 초기화
                    for (i in 0..8) {
                        when {
                            i == 1 -> {
                                suggestionInventory.setItem(i, null)
                            }
                            i != 7 -> {
                                suggestionInventory.setItem(i, nullItem)
                            }
                            else -> {
                                suggestionInventory.setItem(i, easyIdx)
                            }
                        }
                    }
                    player.openInventory(suggestionInventory)
                    event.player.setCooldown(Material.NETHER_STAR, cooldown * 1200)

                    // 강제로 인벤토리 닫기; Reason = CANT_USE
                    val forceClosingInventory = Runnable {
                        if (suggestionInventory.viewers.contains(player)) {
                            player.closeInventory(InventoryCloseEvent.Reason.CANT_USE)
                        }
                    }

                    // 제안은 1분 안에
                    plugin?.apply {
                        Bukkit.getScheduler().runTaskLater(this, forceClosingInventory, 1200)
                    }

                }
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
        // TODO: 2021-08-21 Handlers 의 Equality Checker 로 핸들러 일치 확인
        if (action == Action.RIGHT_CLICK_AIR &&
            itemInMain.type == Material.NETHER_STAR &&
            itemInMain.itemMeta.displayName() == HandlerNames.SUGGESTION_LIST_HANDLER.component
        ) {
            // TODO: 2021-08-21 UI 개선 -> GUI 로 바꾸기
            val book = Book.book(
                HandlerNames.SUGGESTION_LIST_HANDLER.component,
                Component.text(" ")
            ).toBuilder()

            // TODO: 2021-08-21 시간 계산은 LocalDate 적극적으로 활용하기;; 
            val time = now()
            val timeSec = time.hour * 3600 + time.minute * 60 + time.second
            var dummy = 0
            val page = Component.text()
            for (suggestion in suggester.keys) {
                val suggestionPeriod = suggestionTime[suggestion]!!
                val perSec = suggestionPeriod.hour * 3600 +
                        suggestionPeriod.minute * 60 +
                        suggestionPeriod.second

                val duration = timeSec - perSec

                val suggestionComponent = when (itemMap[suggestion]) {
                    easyPoint -> EasyIndexComponent("${suggestion.name}:\n")
                    intermediatePoint -> IntermediateIndexComponent("${suggestion.name}:\n")
                    hardPoint -> HardIndexComponent("${suggestion.name}:\n")
                    else -> EasyIndexComponent("${suggestion.name}:\n")
                }
                val remains = when (itemMap[suggestion]) {
                    easyPoint -> easyTime * 60 - duration
                    intermediatePoint -> intermediateTime * 60 - duration
                    hardPoint -> hardTime * 60 - duration
                    else -> 0
                }
                val minute = remains / 60
                val second = remains % 60

                page.append(suggestionComponent.getComponent())
                page.append(Component.text("$minute 분 $second 초 남음\n"))

                dummy += 1
                if (dummy > 4) {
                    break
                }
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
                // TODO: 2021-08-21 조금 더 좋은 로직이 있지 않을까?
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

        // Cant use 로 닫힐 경우, 이벤트를 적용하지 않음.
        if (event.reason == InventoryCloseEvent.Reason.CANT_USE) return

        val inventory = event.inventory
        if (inventory == suggestionInventory) {
            // 제안한 아이템과 난이도
            val item = inventory.contents[1] ?: return

            // 네더의 별일 경우 (불가능)
            if (item.type == Material.NETHER_STAR) {
                event.player.sendMessage(AlertComponent("네더의 별은 제안할 수 없습니다.").getComponent())
                return
            }

            // 이미 제안한 아이템일 경우
            if (item.type in itemMap.keys) {
                event.player.sendMessage(AlertComponent("이미 제안이 존재합니다. 아이템은 돌려드리지 않습니다.").getComponent())
                return
            }

            val point = when (inventory.contents[7]?.type) {
                Material.LIME_CONCRETE -> {
                    easyPoint
                }
                Material.YELLOW_CONCRETE -> {
                    intermediatePoint
                }
                Material.RED_CONCRETE -> {
                    hardPoint
                }
                else -> {
                    return
                }
            }
            val time = when (inventory.contents[7]?.type) {
                Material.LIME_CONCRETE -> {
                    easyTime
                }
                Material.YELLOW_CONCRETE -> {
                    intermediateTime
                }
                Material.RED_CONCRETE -> {
                    hardTime
                }
                else -> {
                    return
                }
            }

            // 아이템과 난이도 공표
            // TODO: 2021-08-21 Refactoring: title 부분이 반복적 -> 메소드 묶기
            when (point) {
                easyPoint -> {
                    val name = item.type.name
                    val title = Title.title(
                        EasyIndexComponent("느 집엔 이거 없지?").getComponent(),
                        DescriptionComponent("$name 제출: 시간 제한 ${easyTime}분").getComponent(),
                        Title.Times.of(
                            Ticks.duration(5),
                            Ticks.duration(40),
                            Ticks.duration(5)
                        )
                    )
                    plugin!!.server.onlinePlayers.forEach {
                        it.showTitle(title)
                    }
                }
                intermediatePoint -> {
                    val name = item.type.name
                    val title = Title.title(
                        IntermediateIndexComponent("느 집엔 이거 없지?").getComponent(),
                        DescriptionComponent("$name 제출: 시간 제한 ${intermediateTime}분").getComponent(),
                        Title.Times.of(
                            Ticks.duration(5),
                            Ticks.duration(40),
                            Ticks.duration(5)
                        )
                    )
                    plugin!!.server.onlinePlayers.forEach {
                        it.showTitle(title)
                    }
                }
                hardPoint -> {
                    val name = item.type.name
                    val title = Title.title(
                        HardIndexComponent("느 집엔 이거 없지?").getComponent(),
                        DescriptionComponent("$name 제출: 시간 제한 ${hardTime}분").getComponent(),
                        Title.Times.of(
                            Ticks.duration(5),
                            Ticks.duration(40),
                            Ticks.duration(5)
                        )
                    )
                    plugin!!.server.onlinePlayers.forEach {
                        it.showTitle(title)
                    }
                }
                else -> {
                }
            }

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
                    player.sendMessage(AlertComponent("${item.type.name} 제출 기한 만료!").getComponent())
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
                event.player !in playerDone[itemInMain.type]!! &&
                (event.player != suggester[itemInMain.type])
            ) {
                playerDone[itemInMain.type]!!.add(event.player)
                Bukkit.broadcast(
                    SuccessComponent("${event.player.name}님이 ${itemInMain.type.name}을 제출했습니다!").getComponent()
                )
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