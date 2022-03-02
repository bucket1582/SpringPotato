package com.github.bucket1582.springpotato.suggestion.handlers

import com.github.bucket1582.springpotato.SpringPotato
import com.github.bucket1582.springpotato.basic_logic.handlers.ScoreHandler
import com.github.bucket1582.springpotato.suggestion.exceptions.DuplicatedSuggestionException
import com.github.bucket1582.springpotato.common.difficulty_indicator.EasyIndexComponent
import com.github.bucket1582.springpotato.common.difficulty_indicator.HardIndexComponent
import com.github.bucket1582.springpotato.common.difficulty_indicator.IntermediateIndexComponent
import com.github.bucket1582.springpotato.basic_logic.handlers.GameHandler
import com.github.bucket1582.springpotato.common.difficulty_indicator.tag.*
import com.github.bucket1582.springpotato.common.text_components.AlertComponent
import com.github.bucket1582.springpotato.common.text_components.DescriptionComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.LocalTime
import java.time.LocalTime.now
import java.time.temporal.ChronoUnit

object SuggestionHandler {
    // 제안한 물품 - 제안자
    val suggester: MutableMap<Material, Player> = mutableMapOf()

    // 제안한 물품 - 제출한 사람
    val handedOutPlayer: MutableMap<Material, ArrayList<Player>> = mutableMapOf()

    // 제안한 물품 - 제안한 난이도
    val suggestionDifficulty: MutableMap<Material, DifficultyTag> = mutableMapOf()

    // 제안한 물품 - 제안한 점수
    val suggestionBetting: MutableMap<Material, Int> = mutableMapOf()

    // 제안한 물품 - 제안시 주어진 시간
    val suggestionBettingTime: MutableMap<Material, Int> = mutableMapOf()

    // 제안한 물품 - 제안한 시각
    val suggestionTime: MutableMap<Material, LocalTime> = mutableMapOf()

    fun newSuggestion(
        plugin: com.github.bucket1582.springpotato.SpringPotato, suggestingItem: Material, suggestingPlayer: Player, difficultyIndex: DifficultyTag
    ) {
        val additionalPoint = ScoreHandler.computeAdditionalScore(suggestingPlayer)
        val point = difficultyIndex.getFundamentalPoint() + additionalPoint
        val time = GameHandler.getProgressRatio()?.let { difficultyIndex.getSuggestingTime(GameHandler.getPhase(), it) }
            ?: difficultyIndex.getOriginalSuggestingTime()

        checkSuggestionUnique(suggestingItem)

        // 초기화
        suggester[suggestingItem] = suggestingPlayer
        handedOutPlayer[suggestingItem] = arrayListOf()
        suggestionDifficulty[suggestingItem] = difficultyIndex
        suggestionBetting[suggestingItem] = point
        suggestionBettingTime[suggestingItem] = time
        suggestionTime[suggestingItem] = now()

        startSuggestionCountdown(plugin, suggestingItem, time)
        broadCastSuggestion(plugin.server, suggestingItem, difficultyIndex)
    }

    fun getGUIDescriptionOf(suggestedItem: Material): ItemStack {
        // 제안한 물품은 당연히 제안되었어야 한다.
        assert(suggestedItem in suggester.keys)

        val itemDescription = ItemStack(suggestedItem)
        itemDescription.editMeta {
            when (suggestionDifficulty[suggestedItem]) {
                DifficultyTag.EASY -> it.displayName(EasyIndexComponent(suggestedItem.name).getComponent())
                DifficultyTag.INTERMEDIATE -> it.displayName(IntermediateIndexComponent(suggestedItem.name).getComponent())
                DifficultyTag.HARD -> it.displayName(HardIndexComponent(suggestedItem.name).getComponent())
                else -> it.displayName(EasyIndexComponent(suggestedItem.name).getComponent())
            }
            it.lore(getSuggestionDescription(suggestedItem))
        }

        return itemDescription
    }

    private fun removeSuggestion(suggestingItem: Material) {
        suggester.remove(suggestingItem)
        handedOutPlayer.remove(suggestingItem)
        suggestionDifficulty.remove(suggestingItem)
        suggestionBetting.remove(suggestingItem)
        suggestionBettingTime.remove(suggestingItem)
        suggestionTime.remove(suggestingItem)
    }

    private fun checkSuggestionUnique(suggestingItem: Material): Nothing? {
        if (suggestingItem in suggester.keys) throw DuplicatedSuggestionException("해당 제안이 이미 존재합니다.")
        return null
    }

    private fun startSuggestionCountdown(plugin: com.github.bucket1582.springpotato.SpringPotato, suggestingItem: Material, time: Int) {
        // 제안 만료
        val suggestionExpiration = Runnable {
            if (!GameHandler.isHandOutPhase()) return@Runnable

            Bukkit.broadcast(AlertComponent("$suggestingItem 제출 기한 만료!").getComponent())

            // 제출한 사람들에게 추가 점수를 부여하면서, 제출한 사람 수를 셈.
            val handedOutPlayerCount: Int = plugin.server.onlinePlayers.count {
                if (handedOutPlayer[suggestingItem]?.contains(it) == true) {
                    ScoreHandler.updateScore(it, ScoreHandler.HANDOUT_SCORE)
                    true
                }
                else false
            }

            // 제출한 사람 수에 따른 점수 계산; 감점일 때는 기본 점수만 고려
            if (handedOutPlayerCount == 0) {
                ScoreHandler.updateScore(suggester[suggestingItem]!!, suggestionBetting[suggestingItem]!!)
            } else {
                ScoreHandler.updateScore(
                    suggester[suggestingItem]!!,
                    -handedOutPlayerCount * suggestionDifficulty[suggestingItem]!!.getFundamentalPoint()
                )
            }

            // 제안 만료
            removeSuggestion(suggestingItem)
        }

        Bukkit.getScheduler().runTaskLater(plugin, suggestionExpiration, time.toLong() * 1200)
    }

    private fun getSuggestionDescription(suggestedItem: Material): List<Component> {
        // 제안한 물품은 당연히 제안되었어야 한다.
        assert(suggestedItem in suggester.keys)

        return listOf(
            DescriptionComponent("제안자: ${suggester[suggestedItem]!!.name}").getComponent(),
            DescriptionComponent("남은 시간: ${parseRemainingTime(computeRemainingTime(suggestedItem))}").getComponent(),
            DescriptionComponent("베팅 점수: ${suggestionBetting[suggestedItem]}").getComponent()
        )
    }

    private fun parseRemainingTime(differenceInSeconds: Long): String {
        val minute = differenceInSeconds / 60
        val seconds = differenceInSeconds % 60

        return "${minute}분 ${seconds}초"
    }

    private fun computeRemainingTime(suggestingItem: Material): Long {
        // 제안한 물품은 당연히 제안되었어야 한다.
        assert(suggestingItem in suggestionTime.keys)

        val stdTime = suggestionTime[suggestingItem]!!
        val currentTime = now()

        val totalTimeInSeconds = suggestionBettingTime[suggestingItem]!! * 60

        return totalTimeInSeconds - stdTime.until(currentTime, ChronoUnit.SECONDS)
    }

    private fun broadCastSuggestion(server: Server, suggestingItem: Material, difficultyIndex: DifficultyTag) {
        val name = suggestingItem.name
        val title = Title.title(
            difficultyIndex.getComponent("느 집엔 이거 없지?"),
            DescriptionComponent("$name 제출: 시간 제한 ${suggestionBettingTime[suggestingItem]!!}분").getComponent(),
            Title.Times.of(
                Ticks.duration(5),
                Ticks.duration(40),
                Ticks.duration(5)
            )
        )
        server.showTitle(title)
    }
}