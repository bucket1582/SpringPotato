package com.github.bucket1582.springpotato.wand.handlers

import com.github.bucket1582.springpotato.basic_logic.limitations.LimitationType
import com.github.bucket1582.springpotato.common.text_components.DescriptionComponent
import com.github.bucket1582.springpotato.wand.Wand
import com.github.bucket1582.springpotato.wand.WandNames
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object WandHandler {
    private val playerWandFundamental = mutableListOf<Wand>()

    private val playerWandChoice = mutableMapOf<Player, ArrayList<Wand>>()

    val helperToolList: List<Wand>

    val allWand: List<Wand>

    private const val SUGGESTION_WAND_COOLDOWN = 2 * 1200

    const val TRACKER_WAND_COOLDOWN = 2 * 1200

    private const val VIRTUAL_BED_COOLDOWN = 1 * 1200

    private const val PLAYER_WAND_CHOICE_COUNT_MAX = 2

    val suggestionWand = Wand(
        WandNames.SUGGESTION_WAND,
        listOf(
            DescriptionComponent("우클릭 시 제안 창을 열 수 있습니다.").getComponent()
        ),
        Material.NETHER_STAR,
        SUGGESTION_WAND_COOLDOWN
    )

    val suggestionListWand = Wand(
        WandNames.SUGGESTION_LIST_WAND,
        listOf(
            DescriptionComponent("우클릭 시 제안 목록을 확인할 수 있습니다.").getComponent()
        ),
        Material.BOOK
    )

    val trackWand = Wand(
        WandNames.TRACKER_WAND,
        listOf(
            DescriptionComponent("우클릭 시 무작위 추적 대상을 설정할 수 있습니다.").getComponent()
        ),
        Material.COMPASS,
        TRACKER_WAND_COOLDOWN
    )

    val settingWand = Wand(
        WandNames.SETTING_WAND,
        listOf(
            DescriptionComponent("우클릭 시 게임 중 사용할 도구를 선택할 수 있습니다.").getComponent()
        ),
        Material.NETHER_STAR
    )

    val luckySword = Wand(
        WandNames.LUCKY_SWORD,
        listOf(
            LimitationType.NOT_BREAKING.limitationComponent.getComponent()
        ),
        Material.IRON_SWORD,
        enchantment = Enchantment.LOOT_BONUS_MOBS,
        enchantmentLevel = 3
    )

    val efficientAxe = Wand(
        WandNames.EFFICIENT_AXE,
        listOf(
            LimitationType.NOT_BREAKING.limitationComponent.getComponent()
        ),
        Material.GOLDEN_AXE,
        enchantment = Enchantment.DIG_SPEED,
        enchantmentLevel = 3
    )

    val durablePickaxe = Wand(
        WandNames.DURABLE_PICKAXE,
        listOf(
            LimitationType.NOT_BREAKING.limitationComponent.getComponent()
        ),
        Material.IRON_PICKAXE,
        enchantment = Enchantment.DIG_SPEED,
        enchantmentLevel = 2
    )

    val richnessFishingRod = Wand(
        WandNames.RICHNESS_FISHING_ROD,
        listOf(
            LimitationType.NOT_BREAKING.limitationComponent.getComponent()
        ),
        Material.FISHING_ROD,
        enchantment = Enchantment.LURE,
        enchantmentLevel = 4
    )

    val virtualChest = Wand(
        WandNames.VIRTUAL_CHEST,
        listOf(
            LimitationType.VIRTUAL.limitationComponent.getComponent(),
            DescriptionComponent("우클릭시 9칸짜리 개인 전용 상자를 열 수 있습니다.").getComponent()
        ),
        Material.CHEST
    )

    val virtualBed = Wand(
        WandNames.VIRTUAL_BED,
        listOf(
            LimitationType.VIRTUAL.limitationComponent.getComponent(),
            DescriptionComponent("우클릭시 현재 장소에 스폰 포인트가 저장됩니다.").getComponent()
        ),
        Material.RED_BED,
        VIRTUAL_BED_COOLDOWN
    )

    init {
        helperToolList = listOf(
            luckySword,
            efficientAxe,
            durablePickaxe,
            richnessFishingRod,
            virtualChest,
            virtualBed,
        )

        allWand = listOf(
            suggestionWand,
            suggestionListWand,
            trackWand
        ) + helperToolList
    }

    fun isSuggestionWand(item: ItemStack?): Boolean {
        return isWand(item, suggestionWand)
    }

    fun isSuggestionListWand(item: ItemStack?): Boolean {
        return isWand(item, suggestionListWand)
    }

    fun isTrackerWand(item: ItemStack?): Boolean {
        return isWand(item, trackWand)
    }

    fun isSettingWand(item: ItemStack?): Boolean {
        return isWand(item, settingWand)
    }

    fun isWand(item: ItemStack?): Boolean {
        return allWand.any {
            isWand(item, it)
        }
    }

    fun isWand(item: ItemStack?, wand: Wand): Boolean {
        return item != null && item.type == wand.material
                && item.itemMeta.displayName() == wand.name.component
    }

    fun isUnBreakable(item: ItemStack?): Boolean {
        return item != null &&
                item.itemMeta.lore() != null &&
                item.itemMeta.lore()!!.contains(LimitationType.NOT_BREAKING.limitationComponent.getComponent())
    }

    fun isVirtual(item: ItemStack?): Boolean {
        return item != null &&
                item.itemMeta.lore() != null &&
                item.itemMeta.lore()!!.contains(LimitationType.VIRTUAL.limitationComponent.getComponent())
    }

    fun isWandPlayerChoice(wand: Wand, player: Player): Boolean {
        return playerWandChoice[player] != null && playerWandChoice[player]!!.any { it == wand }
    }

    fun getHelperTool(item: ItemStack): Wand {
        return helperToolList.filter {
            isWand(item, it)
        }[0]
    }

    fun isWandCooldown(player: Player, wand: Wand): Boolean {
        return player.hasCooldown(wand.material)
    }

    fun initSettingPhaseWand() {
        playerWandFundamental.clear()
        playerWandFundamental.add(settingWand)
    }

    fun initMainPhaseWand() {
        playerWandFundamental.clear()
        playerWandFundamental.addAll(listOf(suggestionWand, suggestionListWand, trackWand))
    }

    fun addWand(player: Player, wand: Wand) {
        if (playerWandChoice[player] == null) {
            playerWandChoice[player] = arrayListOf(wand)
            return
        }

        while (playerWandChoice[player]!!.size >= PLAYER_WAND_CHOICE_COUNT_MAX) {
            playerWandChoice[player]!!.removeAt(0)
        }
        playerWandChoice[player]!!.add(wand)
    }

    fun giveWand(player: Player) {
        playerWandFundamental.forEach {
            if (!player.inventory.containsAtLeast(it.getWandAsItemStack(), 1)) {
                player.inventory.addItem(it.getWandAsItemStack())
            }
        }
        playerWandChoice[player]?.forEach {
            if (!player.inventory.containsAtLeast(it.getWandAsItemStack(), 1)) {
                player.inventory.addItem(it.getWandAsItemStack())
            }
        }
    }

    fun cooldownWand(player: Player, wand: Wand) {
        player.setCooldown(wand.material, wand.cooldown)
    }

    fun removeCooldown(player: Player, wand: Wand) {
        player.setCooldown(wand.material, 0)
    }

    fun clearPlayerWand(player: Player) {
        if (playerWandChoice[player] == null) return

        playerWandChoice[player]!!.clear()
    }
}